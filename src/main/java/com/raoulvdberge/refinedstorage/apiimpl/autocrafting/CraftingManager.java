package com.raoulvdberge.refinedstorage.apiimpl.autocrafting;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import com.raoulvdberge.refinedstorage.network.MessageCraftingMonitorElements;
import com.raoulvdberge.refinedstorage.tile.TileController;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CraftingManager implements ICraftingManager {
    private TileController network;

    private Map<String, List<IItemHandlerModifiable>> containerInventories = new LinkedHashMap<>();

    private List<ICraftingPattern> patterns = new ArrayList<>();

    private List<ICraftingTask> tasks = new ArrayList<>();
    private List<ICraftingTask> tasksToAdd = new ArrayList<>();
    private List<ICraftingTask> tasksToCancel = new ArrayList<>();

    private boolean updateRequested;

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

            tasksToCancel.forEach(ICraftingTask::onCancelled);
            tasks.removeAll(tasksToCancel);
            tasksToCancel.clear();

            tasksToAdd.stream().filter(ICraftingTask::isValid).forEach(tasks::add);
            tasksToAdd.clear();

            changed = tasks.removeIf(ICraftingTask::update);

            if (changed) {
                markCraftingMonitorForUpdate();
            }
        }

        if (updateRequested) {
            updateRequested = false;

            sendCraftingMonitorUpdate();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        // TODO
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        // TODO
        return tag;
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

                add(task);

                markCraftingMonitorForUpdate();

                return task;
            }
        }

        return null;
    }

    @Override
    public void track(ItemStack stack, int size) {
        // TODO
    }

    @Override
    public List<ICraftingPattern> getPatterns() {
        return patterns;
    }

    @Override
    public void rebuild() {
        patterns.clear();
        containerInventories.clear();

        for (INetworkNode node : network.getNodeGraph().all()) {
            if (node instanceof ICraftingPatternContainer && node.canUpdate()) {
                ICraftingPatternContainer container = (ICraftingPatternContainer) node;

                patterns.addAll(container.getPatterns());

                IItemHandlerModifiable handler = container.getPatternInventory();
                if (handler != null) {
                    containerInventories.computeIfAbsent(container.getName(), k -> new ArrayList<>()).add(handler);
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

    @Override
    public void markCraftingMonitorForUpdate() {
        this.updateRequested = true;
    }

    @Override
    public void sendCraftingMonitorUpdate() {
        network.world().getMinecraftServer().getPlayerList().getPlayers().stream()
            .filter(player -> player.openContainer instanceof ContainerCraftingMonitor && network.getPosition().equals(((ContainerCraftingMonitor) player.openContainer).getCraftingMonitor().getNetworkPosition()))
            .forEach(player -> RS.INSTANCE.network.sendTo(new MessageCraftingMonitorElements(((ContainerCraftingMonitor) player.openContainer).getCraftingMonitor()), player));
    }

    @Override
    public void sendCraftingMonitorUpdate(EntityPlayerMP player) {
        RS.INSTANCE.network.sendTo(new MessageCraftingMonitorElements(((ContainerCraftingMonitor) player.openContainer).getCraftingMonitor()), player);
    }
}
