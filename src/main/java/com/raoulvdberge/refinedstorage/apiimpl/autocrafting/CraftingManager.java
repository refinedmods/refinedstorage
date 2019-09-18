package com.raoulvdberge.refinedstorage.apiimpl.autocrafting;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternChainList;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTaskError;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.ControllerTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CraftingManager implements ICraftingManager {
    private static final int THROTTLE_DELAY_MS = 3000;

    private static final String NBT_TASKS = "Tasks";
    private static final String NBT_TASK_TYPE = "Type";
    private static final String NBT_TASK_DATA = "Task";

    private ControllerTile network;

    private Map<String, List<IItemHandlerModifiable>> containerInventories = new LinkedHashMap<>();

    private List<ICraftingPattern> patterns = new ArrayList<>();

    private Map<UUID, ICraftingTask> tasks = new LinkedHashMap<>();
    private List<ICraftingTask> tasksToAdd = new ArrayList<>();
    private List<UUID> tasksToCancel = new ArrayList<>();
    private ListNBT tasksToRead;

    private Map<Object, Long> throttledRequesters = new HashMap<>();

    private Set<ICraftingMonitorListener> listeners = new HashSet<>();

    public CraftingManager(ControllerTile network) {
        this.network = network;
    }

    @Override
    public Collection<ICraftingTask> getTasks() {
        return tasks.values();
    }

    @Override
    @Nullable
    public ICraftingTask getTask(UUID id) {
        return tasks.get(id);
    }

    @Override
    public Map<String, List<IItemHandlerModifiable>> getNamedContainers() {
        return containerInventories;
    }

    @Override
    public void add(@Nonnull ICraftingTask task) {
        tasksToAdd.add(task);

        network.markDirty();
    }

    @Override
    public void cancel(@Nullable UUID id) {
        if (id == null) {
            tasksToCancel.addAll(tasks.keySet());
        } else {
            tasksToCancel.add(id);
        }

        network.markDirty();
    }

    @Override
    @Nullable
    public ICraftingTask create(ItemStack stack, int quantity) {
        ICraftingPattern pattern = getPattern(stack);
        if (pattern == null) {
            return null;
        }

        ICraftingTaskFactory factory = API.instance().getCraftingTaskRegistry().get(pattern.getId());
        if (factory == null) {
            return null;
        }

        return factory.create(network, API.instance().createCraftingRequestInfo(stack), quantity, pattern);
    }

    @Nullable
    @Override
    public ICraftingTask create(FluidStack stack, int quantity) {
        ICraftingPattern pattern = getPattern(stack);
        if (pattern == null) {
            return null;
        }

        ICraftingTaskFactory factory = API.instance().getCraftingTaskRegistry().get(pattern.getId());
        if (factory == null) {
            return null;
        }

        return factory.create(network, API.instance().createCraftingRequestInfo(stack), quantity, pattern);
    }

    @Override
    public ICraftingPatternChainList createPatternChainList() {
        return new CraftingPatternChainList(patterns);
    }

    @Override
    public void update() {
        if (network.canRun()) {
            if (tasksToRead != null) {
                for (int i = 0; i < tasksToRead.size(); ++i) {
                    CompoundNBT taskTag = tasksToRead.getCompound(i);

                    String taskType = taskTag.getString(NBT_TASK_TYPE);
                    CompoundNBT taskData = taskTag.getCompound(NBT_TASK_DATA);

                    ICraftingTaskFactory factory = API.instance().getCraftingTaskRegistry().get(taskType);
                    if (factory != null) {
                        try {
                            ICraftingTask task = factory.createFromNbt(network, taskData);

                            tasks.put(task.getId(), task);
                        } catch (CraftingTaskReadException e) {
                            e.printStackTrace();
                        }
                    }
                }

                this.tasksToRead = null;
            }

            boolean changed = !tasksToCancel.isEmpty() || !tasksToAdd.isEmpty();

            for (UUID idToCancel : tasksToCancel) {
                if (this.tasks.containsKey(idToCancel)) {
                    this.tasks.get(idToCancel).onCancelled();
                    this.tasks.remove(idToCancel);
                }
            }
            this.tasksToCancel.clear();

            for (ICraftingTask task : this.tasksToAdd) {
                this.tasks.put(task.getId(), task);
            }
            this.tasksToAdd.clear();

            boolean anyFinished = false;

            Iterator<Map.Entry<UUID, ICraftingTask>> it = tasks.entrySet().iterator();
            while (it.hasNext()) {
                ICraftingTask task = it.next().getValue();

                if (task.update()) {
                    anyFinished = true;

                    it.remove();
                }
            }

            if (changed || anyFinished) {
                onTaskChanged();
            }

            if (!tasks.isEmpty()) {
                network.markDirty();
            }
        }
    }

    @Override
    public void readFromNbt(CompoundNBT tag) {
        this.tasksToRead = tag.getList(NBT_TASKS, Constants.NBT.TAG_COMPOUND);
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        ListNBT list = new ListNBT();

        for (ICraftingTask task : tasks.values()) {
            CompoundNBT taskTag = new CompoundNBT();

            taskTag.putString(NBT_TASK_TYPE, task.getPattern().getId());
            taskTag.put(NBT_TASK_DATA, task.writeToNbt(new CompoundNBT()));

            list.add(taskTag);
        }

        tag.put(NBT_TASKS, list);

        return tag;
    }

    @Override
    public void addListener(ICraftingMonitorListener listener) {
        listeners.add(listener);

        listener.onAttached();
    }

    @Override
    public void removeListener(ICraftingMonitorListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onTaskChanged() {
        listeners.forEach(ICraftingMonitorListener::onChanged);
    }

    @Override
    @Nullable
    public ICraftingTask request(Object source, ItemStack stack, int amount) {
        if (isThrottled(source)) {
            return null;
        }

        for (ICraftingTask task : getTasks()) {
            if (task.getRequested().getItem() != null) {
                if (API.instance().getComparer().isEqualNoQuantity(task.getRequested().getItem(), stack)) {
                    amount -= task.getQuantity();
                }
            }
        }

        if (amount > 0) {
            ICraftingTask task = create(stack, amount);

            if (task != null) {
                ICraftingTaskError error = task.calculate();

                if (error == null && !task.hasMissing()) {
                    this.add(task);

                    return task;
                } else {
                    throttle(source);
                }
            } else {
                throttle(source);
            }
        }

        return null;
    }

    @Nullable
    @Override
    public ICraftingTask request(Object source, FluidStack stack, int amount) {
        if (isThrottled(source)) {
            return null;
        }

        for (ICraftingTask task : getTasks()) {
            if (task.getRequested().getFluid() != null) {
                if (API.instance().getComparer().isEqual(task.getRequested().getFluid(), stack, IComparer.COMPARE_NBT)) {
                    amount -= task.getQuantity();
                }
            }
        }

        if (amount > 0) {
            ICraftingTask task = create(stack, amount);

            if (task != null) {
                ICraftingTaskError error = task.calculate();

                if (error == null && !task.hasMissing()) {
                    this.add(task);

                    return task;
                } else {
                    throttle(source);
                }
            } else {
                throttle(source);
            }
        }

        return null;
    }

    private void throttle(Object source) {
        if (source != null) {
            throttledRequesters.put(source, System.currentTimeMillis());
        }
    }

    private boolean isThrottled(Object source) {
        if (source == null) {
            return false;
        }

        Long throttledSince = throttledRequesters.get(source);
        if (throttledSince == null) {
            return false;
        }

        return System.currentTimeMillis() - throttledSince < THROTTLE_DELAY_MS;
    }

    @Override
    public int track(ItemStack stack, int size) {
        for (ICraftingTask task : tasks.values()) {
            size = task.onTrackedInsert(stack, size);

            if (size == 0) {
                return 0;
            }
        }

        return size;
    }

    @Override
    public int track(FluidStack stack, int size) {
        for (ICraftingTask task : tasks.values()) {
            size = task.onTrackedInsert(stack, size);

            if (size == 0) {
                return 0;
            }
        }

        return size;
    }

    @Override
    public List<ICraftingPattern> getPatterns() {
        return patterns;
    }

    @Override
    public void rebuild() {
        this.patterns.clear();
        this.containerInventories.clear();

        List<ICraftingPatternContainer> containers = new ArrayList<>();

        for (INetworkNode node : network.getNodeGraph().all()) {
            if (node instanceof ICraftingPatternContainer && node.canUpdate()) {
                containers.add((ICraftingPatternContainer) node);
            }
        }

        containers.sort((a, b) -> b.getPosition().compareTo(a.getPosition()));

        for (ICraftingPatternContainer container : containers) {
            this.patterns.addAll(container.getPatterns());

            IItemHandlerModifiable handler = container.getPatternInventory();
            if (handler != null) {
                this.containerInventories.computeIfAbsent(container.getName(), k -> new ArrayList<>()).add(handler);
            }
        }
    }

    @Nullable
    @Override
    public ICraftingPattern getPattern(ItemStack pattern) {
        for (ICraftingPattern patternInList : patterns) {
            for (ItemStack output : patternInList.getOutputs()) {
                if (API.instance().getComparer().isEqualNoQuantity(output, pattern)) {
                    return patternInList;
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public ICraftingPattern getPattern(FluidStack pattern) {
        for (ICraftingPattern patternInList : patterns) {
            for (FluidStack output : patternInList.getFluidOutputs()) {
                if (API.instance().getComparer().isEqual(output, pattern, IComparer.COMPARE_NBT)) {
                    return patternInList;
                }
            }
        }

        return null;
    }
}
