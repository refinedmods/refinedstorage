package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.monitor.CraftingMonitorElementFactory;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.Node;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.NodeList;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.NodeListener;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.ProcessingNode;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.FluidStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.ItemStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.FluidStorageDiskFactory;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.ItemStorageDiskFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CraftingTask implements ICraftingTask, NodeListener {
    private static final String NBT_REQUESTED = "Requested";
    private static final String NBT_QUANTITY = "Quantity";
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_TICKS = "Ticks";
    private static final String NBT_ID = "Id";
    private static final String NBT_EXECUTION_STARTED = "ExecutionStarted";
    private static final String NBT_INTERNAL_STORAGE = "InternalStorage";
    private static final String NBT_INTERNAL_FLUID_STORAGE = "InternalFluidStorage";
    private static final String NBT_TO_EXTRACT_INITIAL = "ToExtractInitial";
    private static final String NBT_TO_EXTRACT_INITIAL_FLUIDS = "ToExtractInitialFluids";
    private static final String NBT_CRAFTS = "Crafts";
    private static final String NBT_TOTAL_STEPS = "TotalSteps";
    private static final String NBT_CURRENT_STEP = "CurrentStep";

    private final IStorageDisk<ItemStack> internalStorage;
    private final IStorageDisk<FluidStack> internalFluidStorage;

    private final INetwork network;
    private final ICraftingRequestInfo requested;
    private final int quantity;
    private final ICraftingPattern pattern;
    private final UUID id;
    private final NodeList nodes;
    private final IStackList<ItemStack> toExtractInitial;
    private final IStackList<FluidStack> toExtractInitialFluids;
    private final CraftingMonitorElementFactory craftingMonitorElementFactory = new CraftingMonitorElementFactory();
    private int ticks;
    private long startTime = -1;
    private int totalSteps;
    private int currentStep;

    public CraftingTask(INetwork network,
                        ICraftingRequestInfo requested,
                        int quantity,
                        ICraftingPattern pattern,
                        NodeList nodes,
                        IStackList<ItemStack> toExtractInitial,
                        IStackList<FluidStack> toExtractInitialFluids) {
        this.network = network;

        this.requested = requested;
        this.quantity = quantity;
        this.pattern = pattern;
        this.id = UUID.randomUUID();
        this.nodes = nodes;

        this.internalStorage = new ItemStorageDisk(null, -1, null);
        this.internalFluidStorage = new FluidStorageDisk(null, -1, null);

        this.toExtractInitial = toExtractInitial;
        this.toExtractInitialFluids = toExtractInitialFluids;
    }

    public CraftingTask(INetwork network, CompoundTag tag) throws CraftingTaskReadException {
        this.network = network;

        this.requested = API.instance().createCraftingRequestInfo(tag.getCompound(NBT_REQUESTED));
        this.quantity = tag.getInt(NBT_QUANTITY);
        this.pattern = SerializationUtil.readPatternFromNbt(tag.getCompound(NBT_PATTERN), network.getLevel());
        this.id = tag.getUUID(NBT_ID);
        this.nodes = new NodeList();

        this.ticks = tag.getInt(NBT_TICKS);
        this.startTime = tag.getLong(NBT_EXECUTION_STARTED);
        this.totalSteps = tag.getInt(NBT_TOTAL_STEPS);
        this.currentStep = tag.getInt(NBT_CURRENT_STEP);

        this.internalStorage = new ItemStorageDiskFactory().createFromNbt(null, tag.getCompound(NBT_INTERNAL_STORAGE));
        this.internalFluidStorage = new FluidStorageDiskFactory().createFromNbt(null, tag.getCompound(NBT_INTERNAL_FLUID_STORAGE));

        this.toExtractInitial = SerializationUtil.readItemStackList(tag.getList(NBT_TO_EXTRACT_INITIAL, Tag.TAG_COMPOUND));
        this.toExtractInitialFluids = SerializationUtil.readFluidStackList(tag.getList(NBT_TO_EXTRACT_INITIAL_FLUIDS, Tag.TAG_COMPOUND));

        ListTag nodeList = tag.getList(NBT_CRAFTS, Tag.TAG_COMPOUND);
        for (int i = 0; i < nodeList.size(); ++i) {
            Node node = Node.fromNbt(network, nodeList.getCompound(i));
            nodes.put(node.getPattern(), node);
        }
    }

    @Override
    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.put(NBT_REQUESTED, requested.writeToNbt());
        tag.putInt(NBT_QUANTITY, quantity);
        tag.put(NBT_PATTERN, SerializationUtil.writePatternToNbt(pattern));
        tag.putInt(NBT_TICKS, ticks);
        tag.putUUID(NBT_ID, id);
        tag.putLong(NBT_EXECUTION_STARTED, startTime);
        tag.put(NBT_INTERNAL_STORAGE, internalStorage.writeToNbt());
        tag.put(NBT_INTERNAL_FLUID_STORAGE, internalFluidStorage.writeToNbt());
        tag.put(NBT_TO_EXTRACT_INITIAL, SerializationUtil.writeItemStackList(toExtractInitial));
        tag.put(NBT_TO_EXTRACT_INITIAL_FLUIDS, SerializationUtil.writeFluidStackList(toExtractInitialFluids));
        tag.putInt(NBT_TOTAL_STEPS, totalSteps);
        tag.putInt(NBT_CURRENT_STEP, currentStep);

        ListTag nodeList = new ListTag();
        for (Node node : this.nodes.all()) {
            nodeList.add(node.writeToNbt());
        }
        tag.put(NBT_CRAFTS, nodeList);

        return tag;
    }

    @Override
    public void start() {
        nodes.all().forEach(node -> {
            totalSteps += node.getQuantity();

            node.onCalculationFinished();
        });

        startTime = System.currentTimeMillis();

        IoUtil.extractItemsFromNetwork(toExtractInitial, network, internalStorage);
        IoUtil.extractFluidsFromNetwork(toExtractInitialFluids, network, internalFluidStorage);
    }

    @Override
    public int getCompletionPercentage() {
        if (totalSteps == 0) {
            return 0;
        }

        return (int) ((float) currentStep * 100 / totalSteps);
    }

    @Override
    public boolean update() {
        ++ticks;

        if (nodes.isEmpty()) {
            List<Runnable> toPerform = new ArrayList<>();

            for (ItemStack stack : internalStorage.getStacks()) {
                ItemStack remainder = network.insertItem(stack, stack.getCount(), Action.PERFORM);

                toPerform.add(() -> internalStorage.extract(stack, stack.getCount() - remainder.getCount(), IComparer.COMPARE_NBT, Action.PERFORM));
            }

            for (FluidStack stack : internalFluidStorage.getStacks()) {
                FluidStack remainder = network.insertFluid(stack, stack.getAmount(), Action.PERFORM);

                toPerform.add(() -> internalFluidStorage.extract(stack, stack.getAmount() - remainder.getAmount(), IComparer.COMPARE_NBT, Action.PERFORM));
            }

            // Prevent CME.
            toPerform.forEach(Runnable::run);

            return internalStorage.getStacks().isEmpty() && internalFluidStorage.getStacks().isEmpty();
        } else {
            IoUtil.extractItemsFromNetwork(toExtractInitial, network, internalStorage);
            IoUtil.extractFluidsFromNetwork(toExtractInitialFluids, network, internalFluidStorage);

            for (Node node : nodes.all()) {
                node.update(network, ticks, nodes, internalStorage, internalFluidStorage, this);
            }

            nodes.removeMarkedForRemoval();

            return false;
        }
    }

    @Override
    public void onCancelled() {
        nodes.unlockAll(network);

        for (ItemStack remainder : internalStorage.getStacks()) {
            network.insertItem(remainder, remainder.getCount(), Action.PERFORM);
        }

        for (FluidStack remainder : internalFluidStorage.getStacks()) {
            network.insertFluid(remainder, remainder.getAmount(), Action.PERFORM);
        }
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public ICraftingRequestInfo getRequested() {
        return requested;
    }

    @Override
    public int onTrackedInsert(ItemStack stack, int size) {
        for (Node node : this.nodes.all()) {
            if (node instanceof ProcessingNode) {
                ProcessingNode processing = (ProcessingNode) node;

                int needed = processing.getNeeded(stack);
                if (needed > 0) {
                    if (needed > size) {
                        needed = size;
                    }

                    processing.markReceived(stack, needed);

                    size -= needed;

                    if (!processing.isRoot()) {
                        internalStorage.insert(stack, needed, Action.PERFORM);
                    } else {
                        ItemStack remainder = network.insertItem(stack, needed, Action.PERFORM);

                        internalStorage.insert(remainder, remainder.getCount(), Action.PERFORM);
                    }

                    network.getCraftingManager().onTaskChanged();

                    if (size == 0) {
                        return 0;
                    }
                }
            }
        }

        return size;
    }

    @Override
    public int onTrackedInsert(FluidStack stack, int size) {
        for (Node node : this.nodes.all()) {
            if (node instanceof ProcessingNode) {
                ProcessingNode processing = (ProcessingNode) node;

                int needed = processing.getNeeded(stack);

                if (needed > 0) {
                    if (needed > size) {
                        needed = size;
                    }

                    processing.markReceived(stack, needed);

                    size -= needed;

                    if (!processing.isRoot()) {
                        internalFluidStorage.insert(stack, needed, Action.PERFORM);
                    } else {
                        FluidStack remainder = network.insertFluid(stack, needed, Action.PERFORM);

                        internalFluidStorage.insert(remainder, remainder.getAmount(), Action.PERFORM);
                    }

                    network.getCraftingManager().onTaskChanged();

                    if (size == 0) {
                        return 0;
                    }
                }
            }
        }

        return size;
    }

    @Override
    public List<ICraftingMonitorElement> getCraftingMonitorElements() {
        return craftingMonitorElementFactory.getElements(nodes.all(), internalStorage, internalFluidStorage);
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void onAllDone(Node node) {
        nodes.remove(node);
    }

    @Override
    public void onSingleDone(Node node) {
        currentStep++;
        network.getCraftingManager().onTaskChanged();
    }
}
