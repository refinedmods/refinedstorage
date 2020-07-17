package com.refinedmods.refinedstorage.apiimpl.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternChainList;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskError;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskFactory;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.CraftingTask;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CraftingManager implements ICraftingManager {
    private static final int THROTTLE_DELAY_MS = 3000;

    private static final Logger LOGGER = LogManager.getLogger(CraftingManager.class);

    private static final String NBT_TASKS = "Tasks";
    private static final String NBT_TASK_TYPE = "Type";
    private static final String NBT_TASK_DATA = "Task";

    private final INetwork network;

    private final Map<ITextComponent, List<IItemHandlerModifiable>> containerInventories = new LinkedHashMap<>();
    private final Map<ICraftingPattern, Set<ICraftingPatternContainer>> patternToContainer = new HashMap<>();

    private final List<ICraftingPattern> patterns = new ArrayList<>();

    private final Map<UUID, ICraftingTask> tasks = new LinkedHashMap<>();
    private final List<ICraftingTask> tasksToAdd = new ArrayList<>();
    private final List<UUID> tasksToCancel = new ArrayList<>();
    private ListNBT tasksToRead;

    private final Map<Object, Long> throttledRequesters = new HashMap<>();

    private final Set<ICraftingMonitorListener> listeners = new HashSet<>();

    public CraftingManager(INetwork network) {
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
    public Map<ITextComponent, List<IItemHandlerModifiable>> getNamedContainers() {
        return containerInventories;
    }

    @Override
    public void start(@Nonnull ICraftingTask task) {
        task.start();
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

        ICraftingTaskFactory factory = API.instance().getCraftingTaskRegistry().get(pattern.getCraftingTaskFactoryId());
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

        ICraftingTaskFactory factory = API.instance().getCraftingTaskRegistry().get(pattern.getCraftingTaskFactoryId());
        if (factory == null) {
            return null;
        }

        return factory.create(network, API.instance().createCraftingRequestInfo(stack), quantity, pattern);
    }

    @Override
    public void update() {
        if (network.canRun()) {
            if (tasksToRead != null) {
                for (int i = 0; i < tasksToRead.size(); ++i) {
                    CompoundNBT taskTag = tasksToRead.getCompound(i);

                    ResourceLocation taskType = new ResourceLocation(taskTag.getString(NBT_TASK_TYPE));
                    CompoundNBT taskData = taskTag.getCompound(NBT_TASK_DATA);

                    ICraftingTaskFactory factory = API.instance().getCraftingTaskRegistry().get(taskType);
                    if (factory != null) {
                        try {
                            ICraftingTask task = factory.createFromNbt(network, taskData);

                            tasks.put(task.getId(), task);
                        } catch (CraftingTaskReadException e) {
                            LOGGER.error("Could not deserialize crafting task", e);
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

            taskTag.putString(NBT_TASK_TYPE, task.getPattern().getCraftingTaskFactoryId().toString());
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
                    this.start(task);

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
                    this.start(task);

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
    public int track(@Nonnull ItemStack stack, int size) {
        if (stack.isEmpty()) {
            return 0;
        }

        for (ICraftingTask task : tasks.values()) {
            size = task.onTrackedInsert(stack, size);

            if (size == 0) {
                return 0;
            }
        }

        return size;
    }

    @Override
    public int track(@Nonnull FluidStack stack, int size) {
        if (stack.isEmpty()) {
            return 0;
        }

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
    public void invalidate() {
        this.network.getItemStorageCache().getCraftablesList().clear();
        this.network.getFluidStorageCache().getCraftablesList().clear();

        this.patterns.clear();
        this.containerInventories.clear();
        this.patternToContainer.clear();

        List<ICraftingPatternContainer> containers = new ArrayList<>();

        for (INetworkNode node : network.getNodeGraph().all()) {
            if (node instanceof ICraftingPatternContainer && node.isActive()) {
                containers.add((ICraftingPatternContainer) node);
            }
        }

        containers.sort((a, b) -> b.getPosition().compareTo(a.getPosition()));

        for (ICraftingPatternContainer container : containers) {
            for (ICraftingPattern pattern : container.getPatterns()) {
                this.patterns.add(pattern);

                for (ItemStack output : pattern.getOutputs()) {
                    network.getItemStorageCache().getCraftablesList().add(output);
                }

                for (FluidStack output : pattern.getFluidOutputs()) {
                    network.getFluidStorageCache().getCraftablesList().add(output);
                }

                Set<ICraftingPatternContainer> list = this.patternToContainer.get(pattern);
                if (list == null) {
                    list = new LinkedHashSet<>();
                }
                list.add(container);
                this.patternToContainer.put(pattern, list);
            }

            IItemHandlerModifiable handler = container.getPatternInventory();
            if (handler != null) {
                this.containerInventories.computeIfAbsent(container.getName(), k -> new ArrayList<>()).add(handler);
            }
        }

        this.network.getItemStorageCache().reAttachListeners();
        this.network.getFluidStorageCache().reAttachListeners();
    }

    @Override
    public Set<ICraftingPatternContainer> getAllContainer(ICraftingPattern pattern) {
        return patternToContainer.getOrDefault(pattern, new LinkedHashSet<>());
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
