package com.raoulvdberge.refinedstorage.apiimpl.autocrafting;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorListener;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.TileController;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CraftingManager implements ICraftingManager {
    private TileController network;

    private Map<String, List<IItemHandlerModifiable>> containerInventories = new LinkedHashMap<>();

    private List<ICraftingPattern> patterns = new ArrayList<>();

    private List<ICraftingTask> tasks = new ArrayList<>();
    private List<ICraftingTask> tasksToAdd = new ArrayList<>();
    private List<ICraftingTask> tasksToCancel = new ArrayList<>();

    private Set<ICraftingMonitorListener> listeners = new HashSet<>();

    public CraftingManager(TileController network) {
        this.network = network;
    }

    @Override
    public List<ICraftingTask> getTasks() {
        return tasks;
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
    public void cancel(@Nonnull ICraftingTask task) {
        tasksToCancel.add(task);

        network.markDirty();
    }

    @Override
    @Nullable
    public ICraftingTask create(ItemStack stack, int quantity) {
        ICraftingPattern pattern = getPattern(stack, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);
        if (pattern == null) {
            return null;
        }

        ICraftingTaskFactory factory = API.instance().getCraftingTaskRegistry().get(pattern.getId());
        if (factory == null) {
            return null;
        }

        return factory.create(network, stack, quantity, pattern, null);
    }

    @Override
    public void update() {
        if (network.canRun()) {
            boolean changed = !tasksToCancel.isEmpty() || !tasksToAdd.isEmpty();

            this.tasksToCancel.forEach(ICraftingTask::onCancelled);
            this.tasks.removeAll(tasksToCancel);
            this.tasksToCancel.clear();

            this.tasksToAdd.stream().filter(ICraftingTask::isValid).forEach(tasks::add);
            this.tasksToAdd.clear();

            boolean anyFinished = tasks.removeIf(ICraftingTask::update);

            if (changed || anyFinished) {
                onTaskChanged();
            }
        }
    }

    @Override
    // TODO
    public void readFromNBT(NBTTagCompound tag) {
    }

    @Override
    // TODO
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
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
    public ICraftingTask schedule(ItemStack stack, int toSchedule, int compare) {
        for (ICraftingTask task : getTasks()) {
            for (ItemStack output : task.getPattern().getOutputs()) {
                if (API.instance().getComparer().isEqual(output, stack, compare)) {
                    toSchedule -= output.getCount() * task.getQuantity();
                }
            }
        }

        if (toSchedule > 0) {
            ICraftingTask task = create(stack, toSchedule);

            if (task != null) {
                task.calculate();

                this.add(task);
                this.onTaskChanged();

                return task;
            }
        }

        return null;
    }

    @Override
    public void track(ItemStack stack, int size) {
        int initialSize = size;

        for (ICraftingTask task : tasks) {
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

        for (INetworkNode node : network.getNodeGraph().all()) {
            if (node instanceof ICraftingPatternContainer && node.canUpdate()) {
                ICraftingPatternContainer container = (ICraftingPatternContainer) node;

                this.patterns.addAll(container.getPatterns());

                IItemHandlerModifiable handler = container.getPatternInventory();
                if (handler != null) {
                    this.containerInventories.computeIfAbsent(container.getName(), k -> new ArrayList<>()).add(handler);
                }
            }
        }
    }

    @Nullable
    @Override
    public ICraftingPattern getPattern(ItemStack pattern, int flags) {
        for (ICraftingPattern patternInList : patterns) {
            for (ItemStack output : patternInList.getOutputs()) {
                if (API.instance().getComparer().isEqual(output, pattern, flags)) {
                    return patternInList;
                }
            }
        }

        return null;
    }
}
