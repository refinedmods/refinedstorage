package com.raoulvdberge.refinedstorage.apiimpl.autocrafting;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.*;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingStep;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import com.raoulvdberge.refinedstorage.network.MessageCraftingMonitorElements;
import com.raoulvdberge.refinedstorage.tile.TileController;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CraftingManager implements ICraftingManager {
    private static final String NBT_CRAFTING_TASKS = "CraftingTasks";

    private TileController network;

    private List<ICraftingPatternContainer> containers = new ArrayList<>();
    private Map<String, List<IItemHandlerModifiable>> containerInventories = new LinkedHashMap<>();
    private CraftingPatternChainList patterns = new CraftingPatternChainList();

    private List<ICraftingTask> craftingTasks = new ArrayList<>();
    private List<ICraftingTask> craftingTasksToAdd = new ArrayList<>();
    private List<ICraftingTask> craftingTasksToCancel = new ArrayList<>();
    private List<NBTTagCompound> craftingTasksToRead = new ArrayList<>();
    private List<ICraftingStep> runningSteps = new ArrayList<>();

    private boolean craftingMonitorUpdateRequested;

    private int ticks;

    public CraftingManager(TileController network) {
        this.network = network;
    }

    @Override
    public List<ICraftingTask> getTasks() {
        return craftingTasks;
    }

    @Override
    public List<ICraftingPatternContainer> getContainers() {
        return containers;
    }

    @Override
    public Map<String, List<IItemHandlerModifiable>> getNamedContainers() {
        return containerInventories;
    }

    @Override
    public void add(@Nonnull ICraftingTask task) {
        craftingTasksToAdd.add(task);

        network.markDirty();
    }

    @Override
    public void cancel(@Nonnull ICraftingTask task) {
        craftingTasksToCancel.add(task);

        network.markDirty();
    }

    @Override
    public ICraftingTask create(@Nullable ItemStack stack, ICraftingPattern pattern, int quantity, boolean automated) {
        return API.instance().getCraftingTaskRegistry().get(pattern.getId()).create(network, stack, pattern, quantity, automated, null);
    }

    @Override
    public ICraftingTask create(@Nullable ItemStack stack, ICraftingPatternChain patternChain, int quantity, boolean automated) {
        return API.instance().getCraftingTaskRegistry().get(patternChain.getPrototype().getId()).create(network, stack, patternChain, quantity, automated);
    }

    @Override
    public List<ICraftingPattern> getPatterns() {
        return patterns.asList();
    }

    @Override
    public List<ICraftingPattern> getPatterns(ItemStack pattern, int flags) {
        return getPatternChains(pattern, flags).stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    private List<ICraftingPatternChain> getPatternChains(ItemStack pattern, int flags) {
        List<ICraftingPatternChain> patternChains = new LinkedList<>();

        for (CraftingPatternChainList.CraftingPatternChain chain : this.patterns) {
            for (ItemStack output : chain.getPrototype().getOutputs()) {
                if (API.instance().getComparer().isEqual(output, pattern, flags)) {
                    patternChains.add(chain);
                }
            }
        }

        return patternChains;
    }

    @Override
    public boolean hasPattern(ItemStack stack, int flags) {
        for (CraftingPatternChainList.CraftingPatternChain chain : this.patterns) {
            for (ItemStack output : chain.getPrototype().getOutputs()) {
                if (API.instance().getComparer().isEqual(output, stack, flags)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ICraftingPatternChain getPatternChain(ItemStack pattern, int flags) {
        return getPatternChain(pattern, flags, network.getItemStorageCache().getList().getOredicted());
    }

    @Override
    public ICraftingPatternChain getPatternChain(ItemStack pattern, int flags, IStackList<ItemStack> itemList) {
        List<ICraftingPatternChain> patternChains = getPatternChains(pattern, flags);

        if (patternChains.isEmpty()) {
            return null;
        } else if (patternChains.size() == 1) {
            return patternChains.get(0);
        }

        int highestScore = 0;
        int highestPattern = 0;

        for (int i = 0; i < patternChains.size(); ++i) {
            int score = 0;

            for (ItemStack input : patternChains.get(i).getPrototype().getInputs()) {
                if (input != null) {
                    ItemStack stored = itemList.get(input, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT | (patternChains.get(i).getPrototype().isOredict() ? IComparer.COMPARE_OREDICT : 0));

                    score += stored != null ? stored.getCount() : 0;
                }
            }

            if (score > highestScore) {
                highestScore = score;
                highestPattern = i;
            }
        }

        return patternChains.get(highestPattern);
    }

    @Override
    public void update() {
        if (!craftingTasksToRead.isEmpty()) {
            for (NBTTagCompound tag : craftingTasksToRead) {
                ICraftingTask task = readCraftingTask(network, tag);

                if (task != null) {
                    add(task);
                }
            }

            craftingTasksToRead.clear();
        }

        if (network.canRun()) {
            boolean craftingTasksChanged = !craftingTasksToAdd.isEmpty() || !craftingTasksToCancel.isEmpty();

            craftingTasksToCancel.forEach(ICraftingTask::onCancelled);
            craftingTasks.removeAll(craftingTasksToCancel);
            craftingTasksToCancel.clear();

            craftingTasksToAdd.stream().filter(ICraftingTask::isValid).forEach(craftingTasks::add);
            craftingTasksToAdd.clear();

            // Only run task updates every 5 ticks
            if (ticks++ % 5 == 0) {
                Iterator<ICraftingTask> craftingTaskIterator = craftingTasks.iterator();
                Map<ICraftingPatternContainer, Integer> usedCrafters = new HashMap<>();

                while (craftingTaskIterator.hasNext()) {
                    ICraftingTask task = craftingTaskIterator.next();

                    if (task.update(usedCrafters)) {
                        EventAutocraftingComplete.fire(network, task.getRequested(), task.getQuantity());
                        craftingTaskIterator.remove();

                        craftingTasksChanged = true;
                    } else if (!task.getMissing().isEmpty() && ticks % 100 == 0 && Math.random() > 0.5) {
                        task.getMissing().clear();
                    }
                }

                runningSteps = craftingTasks.stream()
                    .map(ICraftingTask::getSteps)
                    .flatMap(List::stream)
                    .filter(ICraftingStep::hasStartedProcessing)
                    .collect(Collectors.toList());

                if (craftingTasksChanged) {
                    markCraftingMonitorForUpdate();
                }
            }
        }

        if (craftingMonitorUpdateRequested) {
            craftingMonitorUpdateRequested = false;

            sendCraftingMonitorUpdate();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey(NBT_CRAFTING_TASKS)) {
            NBTTagList taskList = tag.getTagList(NBT_CRAFTING_TASKS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < taskList.tagCount(); ++i) {
                craftingTasksToRead.add(taskList.getCompoundTagAt(i));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList craftingTaskList = new NBTTagList();

        for (ICraftingTask task : craftingTasks) {
            craftingTaskList.appendTag(task.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_CRAFTING_TASKS, craftingTaskList);

        return tag;
    }

    @Override
    public ICraftingTask schedule(ItemStack stack, int toSchedule, int compare) {
        for (ICraftingTask task : getTasks()) {
            for (ItemStack output : task.getPattern().getOutputs()) {
                if (API.instance().getComparer().isEqual(output, stack, compare)) {
                    toSchedule -= output.getCount() * task.getQuantity();
                }
            }
        }

        if (toSchedule > 0) {
            ICraftingPatternChain patternChain = getPatternChain(stack, compare);

            if (patternChain != null) {
                ICraftingTask task = create(stack, patternChain, toSchedule, true);

                task.calculate();
                task.getMissing().clear();

                add(task);

                markCraftingMonitorForUpdate();

                return task;
            }
        }

        return null;
    }

    @Override
    public void track(ItemStack stack, int size) {
        ItemStack inserted = ItemHandlerHelper.copyStackWithSize(stack, size);

        for (ICraftingStep step : runningSteps) {
            if (step.onReceiveOutput(inserted)) {
                return;
            }
        }
    }

    @Override
    public void rebuild() {
        patterns.clear();
        containerInventories.clear();

        for (INetworkNode node : network.getNodeGraph().all()) {
            if (node instanceof ICraftingPatternContainer && node.canUpdate()) {
                ICraftingPatternContainer container = (ICraftingPatternContainer) node;

                patterns.addAll(container.getPatterns());

                if (!containerInventories.containsKey(container.getName())) {
                    containerInventories.put(container.getName(), new ArrayList<>());
                }

                containerInventories.get(container.getName()).add(container.getPatternInventory());
            }
        }

        // Auto reschedules stuck tasks after a pattern rebuild
        craftingTasks.forEach(t -> t.getMissing().clear());
    }

    @Override
    public void markCraftingMonitorForUpdate() {
        craftingMonitorUpdateRequested = true;
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

    private static ICraftingTask readCraftingTask(INetwork network, NBTTagCompound tag) {
        ItemStack stack = new ItemStack(tag.getCompoundTag(ICraftingTask.NBT_PATTERN_STACK));

        if (!stack.isEmpty() && stack.getItem() instanceof ICraftingPatternProvider) {
            TileEntity container = network.world().getTileEntity(BlockPos.fromLong(tag.getLong(ICraftingTask.NBT_PATTERN_CONTAINER)));

            if (container instanceof INetworkNodeProxy) {
                INetworkNodeProxy proxy = (INetworkNodeProxy) container;
                if (proxy.getNode() instanceof ICraftingPatternContainer) {
                    ICraftingPattern pattern = ((ICraftingPatternProvider) stack.getItem()).create(network.world(), stack, (ICraftingPatternContainer) proxy.getNode());

                    ICraftingTaskFactory factory = API.instance().getCraftingTaskRegistry().get(tag.getString(ICraftingTask.NBT_PATTERN_ID));
                    if (factory != null) {
                        return factory.create(network, tag.hasKey(ICraftingTask.NBT_REQUESTED) ? new ItemStack(tag.getCompoundTag(ICraftingTask.NBT_REQUESTED)) : null, pattern, tag.getInteger(ICraftingTask.NBT_QUANTITY), false, tag);
                    }
                }
            }
        }

        return null;
    }
}
