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
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.TileController;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CraftingManager implements ICraftingManager {
    private static final String NBT_TASKS = "Tasks";
    private static final String NBT_TASK_TYPE = "Type";
    private static final String NBT_TASK_DATA = "Task";

    private TileController network;

    private Map<String, List<IItemHandlerModifiable>> containerInventories = new LinkedHashMap<>();

    private List<ICraftingPattern> patterns = new ArrayList<>();

    private Map<UUID, ICraftingTask> tasks = new LinkedHashMap<>();
    private List<ICraftingTask> tasksToAdd = new ArrayList<>();
    private List<UUID> tasksToCancel = new ArrayList<>();
    private NBTTagList tasksToRead;

    private Set<ICraftingMonitorListener> listeners = new HashSet<>();

    public CraftingManager(TileController network) {
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

        return factory.create(network, stack, quantity, pattern);
    }

    @Override
    public ICraftingPatternChainList createPatternChainList() {
        return new CraftingPatternChainList(patterns);
    }

    @Override
    public void update() {
        if (network.canRun()) {
            if (tasksToRead != null) {
                for (int i = 0; i < tasksToRead.tagCount(); ++i) {
                    NBTTagCompound taskTag = tasksToRead.getCompoundTagAt(i);

                    String taskType = taskTag.getString(NBT_TASK_TYPE);
                    NBTTagCompound taskData = taskTag.getCompoundTag(NBT_TASK_DATA);

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
                this.tasks.get(idToCancel).onCancelled();
                this.tasks.remove(idToCancel);
            }
            this.tasksToCancel.clear();

            this.tasksToAdd.stream().filter(ICraftingTask::isValid).forEach(t -> tasks.put(t.getId(), t));
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
    public void readFromNbt(NBTTagCompound tag) {
        this.tasksToRead = tag.getTagList(NBT_TASKS, Constants.NBT.TAG_COMPOUND);
    }

    @Override
    public NBTTagCompound writeToNbt(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();

        for (ICraftingTask task : tasks.values()) {
            NBTTagCompound taskTag = new NBTTagCompound();

            taskTag.setString(NBT_TASK_TYPE, task.getPattern().getId());
            taskTag.setTag(NBT_TASK_DATA, task.writeToNbt(new NBTTagCompound()));

            list.appendTag(taskTag);
        }

        tag.setTag(NBT_TASKS, list);

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
    public ICraftingTask schedule(ItemStack stack, int toSchedule) {
        for (ICraftingTask task : getTasks()) {
            if (API.instance().getComparer().isEqualNoQuantity(task.getRequested(), stack)) {
                toSchedule -= task.getQuantity();
            }
        }

        ItemStack existing = network.getItemStorageCache().getList().get(stack);

        toSchedule -= existing == null ? 0 : existing.getCount();

        if (toSchedule > 0) {
            ICraftingTask task = create(stack, toSchedule);

            if (task != null) {
                ICraftingTaskError error = task.calculate();

                if (error == null) {
                    this.add(task);

                    return task;
                }
            }
        }

        return null;
    }

    @Override
    public void track(ItemStack stack, int size) {
        int initialSize = size;

        for (ICraftingTask task : tasks.values()) {
            size = task.onTrackedItemInserted(stack, size);

            if (size == 0) {
                break;
            }
        }

        if (size != initialSize) {
            this.onTaskChanged();
        }
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
}
