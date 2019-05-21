package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.google.common.collect.Maps;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.*;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.*;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementError;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementFluidRender;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.craftingmonitor.CraftingMonitorElementItemRender;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementFluidStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewElementItemStack;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFactoryFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFactoryItem;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskItem;
import com.raoulvdberge.refinedstorage.apiimpl.util.OneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CraftingTask implements ICraftingTask {
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
    private static final String NBT_CRAFTING = "Crafting";
    private static final String NBT_PROCESSING = "Processing";
    private static final String NBT_MISSING = "Missing";
    private static final String NBT_MISSING_FLUIDS = "MissingFluids";
    private static final String NBT_TOTAL_STEPS = "TotalSteps";
    private static final String NBT_CONTAINER = "Container";

    private static final String NBT_PATTERN_STACK = "Stack";
    private static final String NBT_PATTERN_CONTAINER_POS = "ContainerPos";

    private static final int DEFAULT_EXTRACT_FLAGS = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;

    private static final Logger LOGGER = LogManager.getLogger();

    private INetwork network;
    private ICraftingRequestInfo requested;
    private int quantity;
    private ICraftingPattern pattern;
    private UUID id = UUID.randomUUID();
    private int ticks;
    private long calculationStarted = -1;
    private long executionStarted = -1;
    private int totalSteps;
    private boolean found;
    private Set<ICraftingPattern> patternsUsed = new HashSet<>();

    private IStorageDisk<ItemStack> internalStorage;
    private IStorageDisk<FluidStack> internalFluidStorage;

    private IStackList<ItemStack> toExtractInitial = API.instance().createItemStackList();
    private IStackList<FluidStack> toExtractInitialFluids = API.instance().createFluidStackList();

    private List<Crafting> crafting = new ArrayList<>();
    private Map<Integer, Processing> processing = new HashMap<>();
    List<Processing> toRemove = new ArrayList<>();

    private IStackList<ItemStack> missing = API.instance().createItemStackList();
    private IStackList<FluidStack> missingFluids = API.instance().createFluidStackList();

    private IStackList<ItemStack> toTake = API.instance().createItemStackList();
    private IStackList<FluidStack> toTakeFluids = API.instance().createFluidStackList();

    private IStackList<ItemStack> toCraft = API.instance().createItemStackList();
    private IStackList<FluidStack> toCraftFluids = API.instance().createFluidStackList();

    public CraftingTask(INetwork network, ICraftingRequestInfo requested, int quantity, ICraftingPattern pattern) {
        this.network = network;
        this.requested = requested;
        this.quantity = quantity;
        this.pattern = pattern;

        this.internalStorage = new StorageDiskItem(network.world(), -1);
        this.internalFluidStorage = new StorageDiskFluid(network.world(), -1);
    }

    public CraftingTask(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        OneSixMigrationHelper.removalHook();

        if (!tag.hasKey(NBT_INTERNAL_STORAGE)) {
            throw new CraftingTaskReadException("Couldn't read crafting task from before RS v1.6.4, skipping...");
        }

        this.network = network;

        this.requested = API.instance().createCraftingRequestInfo(tag.getCompoundTag(NBT_REQUESTED));
        this.quantity = tag.getInteger(NBT_QUANTITY);
        this.pattern = readPatternFromNbt(tag.getCompoundTag(NBT_PATTERN), network.world());
        this.ticks = tag.getInteger(NBT_TICKS);
        this.id = tag.getUniqueId(NBT_ID);
        this.executionStarted = tag.getLong(NBT_EXECUTION_STARTED);

        if (tag.hasKey(NBT_TOTAL_STEPS)) {
            this.totalSteps = tag.getInteger(NBT_TOTAL_STEPS);
        }

        StorageDiskFactoryItem factoryItem = new StorageDiskFactoryItem();
        StorageDiskFactoryFluid factoryFluid = new StorageDiskFactoryFluid();

        this.internalStorage = factoryItem.createFromNbt(network.world(), tag.getCompoundTag(NBT_INTERNAL_STORAGE));
        this.internalFluidStorage = factoryFluid.createFromNbt(network.world(), tag.getCompoundTag(NBT_INTERNAL_FLUID_STORAGE));

        this.toExtractInitial = readItemStackList(tag.getTagList(NBT_TO_EXTRACT_INITIAL, Constants.NBT.TAG_COMPOUND));
        this.toExtractInitialFluids = readFluidStackList(tag.getTagList(NBT_TO_EXTRACT_INITIAL_FLUIDS, Constants.NBT.TAG_COMPOUND));

        NBTTagList craftingList = tag.getTagList(NBT_CRAFTING, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < craftingList.tagCount(); ++i) {
            crafting.add(new Crafting(network, craftingList.getCompoundTagAt(i)));
        }

        NBTTagList processingList = tag.getTagList(NBT_PROCESSING, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < processingList.tagCount(); ++i) {
            processing.put(pattern.getChainHashCode(), new Processing(network, processingList.getCompoundTagAt(i)));
        }

        this.missing = readItemStackList(tag.getTagList(NBT_MISSING, Constants.NBT.TAG_COMPOUND));
        this.missingFluids = readFluidStackList(tag.getTagList(NBT_MISSING_FLUIDS, Constants.NBT.TAG_COMPOUND));
    }

    @Override
    public NBTTagCompound writeToNbt(NBTTagCompound tag) {
        tag.setTag(NBT_REQUESTED, requested.writeToNbt());
        tag.setInteger(NBT_QUANTITY, quantity);
        tag.setTag(NBT_PATTERN, writePatternToNbt(pattern));
        tag.setInteger(NBT_TICKS, ticks);
        tag.setUniqueId(NBT_ID, id);
        tag.setLong(NBT_EXECUTION_STARTED, executionStarted);
        tag.setTag(NBT_INTERNAL_STORAGE, internalStorage.writeToNbt());
        tag.setTag(NBT_INTERNAL_FLUID_STORAGE, internalFluidStorage.writeToNbt());
        tag.setTag(NBT_TO_EXTRACT_INITIAL, writeItemStackList(toExtractInitial));
        tag.setTag(NBT_TO_EXTRACT_INITIAL_FLUIDS, writeFluidStackList(toExtractInitialFluids));
        tag.setInteger(NBT_TOTAL_STEPS, totalSteps);

        NBTTagList craftingList = new NBTTagList();
        for (Crafting crafting : this.crafting) {
            craftingList.appendTag(crafting.writeToNbt());
        }

        tag.setTag(NBT_CRAFTING, craftingList);

        NBTTagList processingList = new NBTTagList();
        for (Processing processing : this.processing.values()) {
            processingList.appendTag(processing.writeToNbt());
        }

        tag.setTag(NBT_PROCESSING, processingList);

        tag.setTag(NBT_MISSING, writeItemStackList(missing));
        tag.setTag(NBT_MISSING_FLUIDS, writeFluidStackList(missingFluids));

        return tag;
    }

    static NBTTagList writeContainerList(List<ICraftingPatternContainer> containers) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < containers.size(); ++i) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setLong(NBT_CONTAINER + i, containers.get(i).getPosition().toLong());
            list.appendTag(tag);
        }
        return list;
    }

    static List<ICraftingPatternContainer> readContainerList(NBTTagList list, World world) {
        List<ICraftingPatternContainer> containers = new ArrayList<>();

        for (int i = 0; i < list.tagCount(); ++i) {
            TileNode node = (TileNode) world.getTileEntity(BlockPos.fromLong(list.getCompoundTagAt(i).getLong(NBT_CONTAINER + i)));
            if (node != null) {
                containers.add((ICraftingPatternContainer) node.getNode());
            }
        }
        return containers;
    }

    static NBTTagList writeItemStackList(IStackList<ItemStack> stacks) {
        NBTTagList list = new NBTTagList();

        for (ItemStack stack : stacks.getStacks()) {
            list.appendTag(StackUtils.serializeStackToNbt(stack));
        }

        return list;
    }

    static IStackList<ItemStack> readItemStackList(NBTTagList list) throws CraftingTaskReadException {
        IStackList<ItemStack> stacks = API.instance().createItemStackList();

        for (int i = 0; i < list.tagCount(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(list.getCompoundTagAt(i));

            if (stack.isEmpty()) {
                throw new CraftingTaskReadException("Empty stack!");
            }

            stacks.add(stack);
        }

        return stacks;
    }

    static NBTTagList writeFluidStackList(IStackList<FluidStack> stacks) {
        NBTTagList list = new NBTTagList();

        for (FluidStack stack : stacks.getStacks()) {
            list.appendTag(stack.writeToNBT(new NBTTagCompound()));
        }

        return list;
    }

    static IStackList<FluidStack> readFluidStackList(NBTTagList list) throws CraftingTaskReadException {
        IStackList<FluidStack> stacks = API.instance().createFluidStackList();

        for (int i = 0; i < list.tagCount(); ++i) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(list.getCompoundTagAt(i));

            if (stack == null) {
                throw new CraftingTaskReadException("Empty stack!");
            }

            stacks.add(stack);
        }

        return stacks;
    }

    @Override
    @Nullable
    public ICraftingTaskError calculate() {
        if (calculationStarted != -1) {
            throw new IllegalStateException("Task already calculated!");
        }

        if (executionStarted != -1) {
            throw new IllegalStateException("Task already started!");
        }

        this.calculationStarted = System.currentTimeMillis();

        int qty = this.quantity;
        int qtyPerCraft = getQuantityPerCraft();
        int crafted = 0;

        IStackList<ItemStack> results = API.instance().createItemStackList();
        IStackList<FluidStack> fluidResults = API.instance().createFluidStackList();

        IStackList<ItemStack> storage = network.getItemStorageCache().getList().copy();
        IStackList<FluidStack> fluidStorage = network.getFluidStorageCache().getList().copy();

        ICraftingPatternChainList patternChainList = network.getCraftingManager().createPatternChainList();

        ICraftingPatternChain patternChain = patternChainList.getChain(pattern);

        while (qty > 0) {
            ICraftingTaskError result = calculateInternal(storage, fluidStorage, results, fluidResults, patternChainList, patternChain.current(), true);

            if (result != null) {
                return result;
            }

            qty -= qtyPerCraft;

            crafted += qtyPerCraft;

            patternChain.cycle();
        }
        for (Processing p : processing.values()) {
            p.setTotals(true);
        }

        if (requested.getItem() != null) {
            this.toCraft.add(requested.getItem(), crafted);
        } else {
            this.toCraftFluids.add(requested.getFluid(), crafted);
        }

        return null;
    }

    class PossibleInputs {
        private List<ItemStack> possibilities;
        private int pos;

        PossibleInputs(List<ItemStack> possibilities) {
            this.possibilities = possibilities;
        }

        ItemStack get() {
            return possibilities.get(pos);
        }

        // Return false if we're exhausted.
        boolean cycle() {
            if (pos + 1 >= possibilities.size()) {
                pos = 0;

                return false;
            }

            pos++;

            return true;
        }

        void sort(IStackList<ItemStack> mutatedStorage, IStackList<ItemStack> results) {
            possibilities.sort((a, b) -> {
                ItemStack ar = mutatedStorage.get(a);
                ItemStack br = mutatedStorage.get(b);

                return (br == null ? 0 : br.getCount()) - (ar == null ? 0 : ar.getCount());
            });

            possibilities.sort((a, b) -> {
                ItemStack ar = results.get(a);
                ItemStack br = results.get(b);

                return (br == null ? 0 : br.getCount()) - (ar == null ? 0 : ar.getCount());
            });
        }
    }

    @Nullable
    private ICraftingTaskError calculateInternal(
        IStackList<ItemStack> mutatedStorage,
        IStackList<FluidStack> mutatedFluidStorage,
        IStackList<ItemStack> results,
        IStackList<FluidStack> fluidResults,
        ICraftingPatternChainList patternChainList,
        ICraftingPattern pattern,
        boolean root) {

        if (System.currentTimeMillis() - calculationStarted > RS.INSTANCE.config.calculationTimeoutMs) {
            return new CraftingTaskError(CraftingTaskErrorType.TOO_COMPLEX);
        }

        if (!patternsUsed.add(pattern)) {
            return new CraftingTaskError(CraftingTaskErrorType.RECURSIVE, pattern);
        }

        IStackList<ItemStack> itemsToExtract = API.instance().createItemStackList();
        IStackList<FluidStack> fluidsToExtract = API.instance().createFluidStackList();

        NonNullList<ItemStack> took = NonNullList.create();

        for (NonNullList<ItemStack> inputs : pattern.getInputs()) {
            if (inputs.isEmpty()) {
                took.add(ItemStack.EMPTY);

                continue;
            }

            PossibleInputs possibleInputs = new PossibleInputs(new ArrayList<>(inputs));
            possibleInputs.sort(mutatedStorage, results);

            ItemStack possibleInput = possibleInputs.get();

            ItemStack fromSelf = results.get(possibleInput);
            ItemStack fromNetwork = mutatedStorage.get(possibleInput);

            took.add(possibleInput);

            int remaining = possibleInput.getCount();

            while (remaining > 0) {
                if (fromSelf != null) {
                    int toTake = Math.min(remaining, fromSelf.getCount());

                    itemsToExtract.add(possibleInput, toTake);

                    results.remove(fromSelf, toTake);

                    remaining -= toTake;

                    fromSelf = results.get(possibleInput);
                } else if (fromNetwork != null) {
                    int toTake = Math.min(remaining, fromNetwork.getCount());

                    this.toTake.add(possibleInput, toTake);

                    itemsToExtract.add(possibleInput, toTake);

                    mutatedStorage.remove(fromNetwork, toTake);

                    remaining -= toTake;

                    fromNetwork = mutatedStorage.get(possibleInput);

                    toExtractInitial.add(took.get(took.size() - 1));
                } else {
                    ICraftingPattern subPattern = network.getCraftingManager().getPattern(possibleInput);

                    if (subPattern != null) {
                        ICraftingPatternChain subPatternChain = patternChainList.getChain(subPattern);

                        while ((fromSelf == null ? 0 : fromSelf.getCount()) < remaining) {
                            ICraftingTaskError result = calculateInternal(mutatedStorage, mutatedFluidStorage, results, fluidResults, patternChainList, subPatternChain.current(), false);

                            if (result != null) {
                                return result;
                            }

                            fromSelf = results.get(possibleInput);
                            if (fromSelf == null) {
                                throw new IllegalStateException("Recursive calculation didn't yield anything");
                            }

                            fromNetwork = mutatedStorage.get(possibleInput);

                            subPatternChain.cycle();
                        }

                        // fromSelf contains the amount crafted after the loop.
                        this.toCraft.add(possibleInput, fromSelf.getCount());
                    } else {
                        if (!possibleInputs.cycle()) {
                            // Give up.
                            possibleInput = possibleInputs.get(); // Revert back to 0.

                            this.missing.add(possibleInput, remaining);

                            itemsToExtract.add(possibleInput, remaining);

                            remaining = 0;
                        } else {
                            // Retry with new input...
                            possibleInput = possibleInputs.get();

                            fromSelf = results.get(possibleInput);
                            fromNetwork = mutatedStorage.get(possibleInput);
                        }
                    }
                }
            }
        }

        for (FluidStack input : pattern.getFluidInputs()) {
            FluidStack fromSelf = fluidResults.get(input, IComparer.COMPARE_NBT);
            FluidStack fromNetwork = mutatedFluidStorage.get(input, IComparer.COMPARE_NBT);

            int remaining = input.amount;

            while (remaining > 0) {
                if (fromSelf != null) {
                    int toTake = Math.min(remaining, fromSelf.amount);

                    fluidsToExtract.add(input, toTake);

                    fluidResults.remove(input, toTake);

                    remaining -= toTake;

                    fromSelf = fluidResults.get(input, IComparer.COMPARE_NBT);
                } else if (fromNetwork != null) {
                    int toTake = Math.min(remaining, fromNetwork.amount);

                    this.toTakeFluids.add(input, toTake);

                    fluidsToExtract.add(input, toTake);

                    mutatedFluidStorage.remove(fromNetwork, toTake);

                    remaining -= toTake;

                    fromNetwork = mutatedFluidStorage.get(input, IComparer.COMPARE_NBT);

                    toExtractInitialFluids.add(input);
                } else {
                    ICraftingPattern subPattern = network.getCraftingManager().getPattern(input);

                    if (subPattern != null) {
                        ICraftingPatternChain subPatternChain = patternChainList.getChain(subPattern);

                        while ((fromSelf == null ? 0 : fromSelf.amount) < remaining) {
                            ICraftingTaskError result = calculateInternal(mutatedStorage, mutatedFluidStorage, results, fluidResults, patternChainList, subPatternChain.current(), false);

                            if (result != null) {
                                return result;
                            }

                            fromSelf = fluidResults.get(input, IComparer.COMPARE_NBT);
                            if (fromSelf == null) {
                                throw new IllegalStateException("Recursive fluid calculation didn't yield anything");
                            }

                            fromNetwork = mutatedFluidStorage.get(input, IComparer.COMPARE_NBT);

                            subPatternChain.cycle();
                        }

                        // fromSelf contains the amount crafted after the loop.
                        this.toCraftFluids.add(input, fromSelf.amount);
                    } else {
                        this.missingFluids.add(input, remaining);

                        fluidsToExtract.add(input, remaining);

                        remaining = 0;
                    }
                }
            }
        }

        patternsUsed.remove(pattern);

        if (pattern.isProcessing()) {
            IStackList<ItemStack> itemsToReceive = API.instance().createItemStackList();
            IStackList<FluidStack> fluidsToReceive = API.instance().createFluidStackList();

            for (ItemStack output : pattern.getOutputs()) {
                results.add(output);

                itemsToReceive.add(output);
            }

            for (FluidStack output : pattern.getFluidOutputs()) {
                fluidResults.add(output);

                fluidsToReceive.add(output);
            }

            if (processing.containsKey(pattern.getChainHashCode())) {
                processing.get(pattern.getChainHashCode()).addQuantity();

                for (ICraftingPatternContainer container : processing.get(pattern.getChainHashCode()).getContainer()) {
                    if (API.instance().isNetworkNodeEqual((INetworkNode) container, pattern.getContainer())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    processing.get(pattern.getChainHashCode()).addContainer(pattern.getContainer());
                }

            } else {
                processing.put(pattern.getChainHashCode(), new Processing(pattern, 1, itemsToReceive, fluidsToReceive, itemsToExtract, fluidsToExtract, root));
            }

        } else {
            if (!fluidsToExtract.isEmpty()) {
                throw new IllegalStateException("Cannot extract fluids in normal pattern!");
            }

            crafting.add(new Crafting(pattern, took, itemsToExtract, root));

            results.add(pattern.getOutput(took));

            for (ItemStack byproduct : pattern.getByproducts(took)) {
                results.add(byproduct);
            }
        }

        return null;
    }

    private void extractInitial() {
        if (!toExtractInitial.isEmpty()) {
            List<ItemStack> toRemove = new ArrayList<>();

            for (ItemStack toExtract : toExtractInitial.getStacks()) {
                ItemStack result = network.extractItem(toExtract, toExtract.getCount(), Action.PERFORM);

                if (result != null) {
                    internalStorage.insert(toExtract, result.getCount(), Action.PERFORM);

                    toRemove.add(result);
                }
            }

            for (ItemStack stack : toRemove) {
                toExtractInitial.remove(stack);
            }

            if (!toRemove.isEmpty()) {
                network.getCraftingManager().onTaskChanged();
            }
        }

        if (!toExtractInitialFluids.isEmpty()) {
            List<FluidStack> toRemove = new ArrayList<>();

            for (FluidStack toExtract : toExtractInitialFluids.getStacks()) {
                FluidStack result = network.extractFluid(toExtract, toExtract.amount, Action.PERFORM);

                if (result != null) {
                    internalFluidStorage.insert(toExtract, result.amount, Action.PERFORM);

                    toRemove.add(result);
                }
            }

            for (FluidStack stack : toRemove) {
                toExtractInitialFluids.remove(stack);
            }

            if (!toRemove.isEmpty()) {
                network.getCraftingManager().onTaskChanged();
            }
        }
    }

    private void updateCrafting() {
        Iterator<Crafting> it = crafting.iterator();

        Map<ICraftingPatternContainer, Integer> counter = Maps.newHashMap();

        while (it.hasNext()) {
            Crafting c = it.next();

            ICraftingPatternContainer container = c.getPattern().getContainer();

            int interval = container.getUpdateInterval();

            if (interval < 0) {
                throw new IllegalStateException(c.getPattern().getContainer() + " has an update interval of < 0");
            }

            if (interval == 0 || ticks % interval == 0) {
                if (counter.getOrDefault(container, 0) == container.getMaximumSuccessfulCraftingUpdates()) {
                    continue;
                }

                boolean hasAll = true;

                for (ItemStack need : c.getToExtract().getStacks()) {
                    ItemStack result = this.internalStorage.extract(need, need.getCount(), DEFAULT_EXTRACT_FLAGS, Action.SIMULATE);

                    if (result == null || result.getCount() != need.getCount()) {
                        hasAll = false;

                        break;
                    }
                }

                if (hasAll) {
                    for (ItemStack need : c.getToExtract().getStacks()) {
                        ItemStack result = this.internalStorage.extract(need, need.getCount(), DEFAULT_EXTRACT_FLAGS, Action.PERFORM);

                        if (result == null || result.getCount() != need.getCount()) {
                            throw new IllegalStateException("Extractor check lied");
                        }
                    }

                    ItemStack output = c.getPattern().getOutput(c.getTook());

                    if (!c.isRoot()) {
                        this.internalStorage.insert(output, output.getCount(), Action.PERFORM);
                    } else {
                        ItemStack remainder = this.network.insertItem(output, output.getCount(), Action.PERFORM);

                        if (remainder != null) {
                            this.internalStorage.insert(remainder, remainder.getCount(), Action.PERFORM);
                        }
                    }

                    // Byproducts need to always be inserted in the internal storage for later reuse further in the task.
                    // Regular outputs can be inserted into the network *IF* it's a root since it's *NOT* expected to be used later on.
                    for (ItemStack byp : c.getPattern().getByproducts(c.getTook())) {
                        this.internalStorage.insert(byp, byp.getCount(), Action.PERFORM);
                    }

                    it.remove();

                    network.getCraftingManager().onTaskChanged();

                    counter.merge(container, 1, (a, b) -> a + b);
                }
            }
        }
    }

    private void updateProcessing() {
        Map<ICraftingPatternContainer, Integer> counter = Maps.newHashMap();

        for (Processing p : processing.values()) {

            for (ICraftingPatternContainer container : p.getContainer()) {

                if (p.getState() == ProcessingState.PROCESSED) {
                    toRemove.add(p);
                    network.getCraftingManager().onTaskChanged();
                    continue;
                }

                int interval = container.getUpdateInterval();

                if (interval < 0) {
                    throw new IllegalStateException(p.getPattern().getContainer() + " has an update interval of < 0");
                }

                if (interval == 0 || ticks % interval == 0) {
                    //TODO: This doesn't happen -> Insertion Speed is slow
                    // Either call x times or insert x times the amount
                    if (counter.getOrDefault(container, 0) == container.getMaximumSuccessfulCraftingUpdates()) {
                        continue;
                    }
                    ProcessingState originalState = p.getState();

                    if (container.isLocked()) {
                        p.setState(ProcessingState.LOCKED);
                    } else {
                        boolean hasAll = true;
                        //Is there a Machine and are there enough items in internal storage
                        for (ItemStack need : p.getItemsToPut().getStacks()) {
                            if (container.getConnectedInventory() == null) {
                                p.setState(ProcessingState.MACHINE_NONE);
                            } else {
                                ItemStack result = this.internalStorage.extract(need, need.getCount(), DEFAULT_EXTRACT_FLAGS, Action.SIMULATE);

                                if (result == null || result.getCount() != need.getCount()) {
                                    hasAll = false;

                                    break;
                                } else {
                                    p.setState(ProcessingState.READY_OR_PROCESSING);
                                }
                            }
                        }

                        if (hasAll && p.getState() == ProcessingState.READY_OR_PROCESSING && !insertIntoInventory(container.getConnectedInventory(), new ArrayDeque<>(p.getItemsToPut().getStacks()), Action.SIMULATE)) {
                            if (p.nothingIsProcessing()) {
                                p.setState(ProcessingState.MACHINE_DOES_NOT_ACCEPT);
                            }
                            break;
                        }


                        for (FluidStack need : p.getFluidsToPut().getStacks()) {
                            if (container.getConnectedFluidInventory() == null) {
                                p.setState(ProcessingState.MACHINE_NONE);
                            } else {
                                FluidStack result = this.internalFluidStorage.extract(need, need.amount, IComparer.COMPARE_NBT, Action.SIMULATE);

                                if (result == null || result.amount != need.amount) {
                                    hasAll = false;

                                    break;
                                } else if (container.getConnectedFluidInventory().fill(result, false) != result.amount) {
                                    if (p.nothingIsProcessing()) {
                                        p.setState(ProcessingState.MACHINE_DOES_NOT_ACCEPT);
                                    }
                                    break;
                                } else if (p.getState() == ProcessingState.READY_OR_PROCESSING || p.getItemsToPut().isEmpty()) { // If the items were ok (or if we didn't have items).
                                    p.setState(ProcessingState.READY_OR_PROCESSING);
                                }
                            }
                        }

                        if (p.getState() == ProcessingState.READY_OR_PROCESSING && hasAll) {
                            Deque<ItemStack> toInsert = new ArrayDeque<>();

                            for (ItemStack need : p.getItemsToPut().getStacks()) {
                                ItemStack result = this.internalStorage.extract(need, need.getCount(), DEFAULT_EXTRACT_FLAGS, Action.PERFORM);
                                if (result == null || result.getCount() != need.getCount()) {
                                    throw new IllegalStateException("The internal crafting inventory reported that " + need + " was available but we got " + result);
                                }

                                toInsert.add(need);
                            }

                            if (!insertIntoInventory(container.getConnectedInventory(), toInsert, Action.PERFORM)) {
                                LOGGER.warn(container.getConnectedInventory() + " unexpectedly didn't accept items, the remainder has been voided!");
                            }

                            for (FluidStack need : p.getFluidsToPut().getStacks()) {
                                FluidStack result = this.internalFluidStorage.extract(need, need.amount, IComparer.COMPARE_NBT, Action.PERFORM);
                                if (result == null || result.amount != need.amount) {
                                    throw new IllegalStateException("The internal crafting inventory reported that " + need + " was available but we got " + result);
                                }

                                int filled = container.getConnectedFluidInventory().fill(result, true);
                                if (filled != result.amount) {
                                    LOGGER.warn(container.getConnectedFluidInventory() + " unexpectedly didn't accept fluids, the remainder has been voided!");
                                }
                            }
                            if (p.getQuantity() != 0) {
                                p.reduceQuantity();
                            } else {
                                p.setState(ProcessingState.PROCESSED);
                            }

                            network.getCraftingManager().onTaskChanged();
                            container.onUsedForProcessing();

                            counter.merge(container, 1, (a, b) -> a + b);
                        }
                    }

                    if (originalState != p.getState()) {
                        network.getCraftingManager().onTaskChanged();
                    }
                }
            }
        }
        for (Processing p : toRemove) {
            processing.remove(p.getPattern().getChainHashCode());
        }
        toRemove.clear();
    }

    private static boolean insertIntoInventory(@Nullable IItemHandler dest, Deque<ItemStack> stacks, Action action) {
        if (dest == null) {
            return false;
        }

        ItemStack current = stacks.poll();

        List<Integer> availableSlots = IntStream.range(0, dest.getSlots()).boxed().collect(Collectors.toList());

        while (current != null && !availableSlots.isEmpty()) {
            ItemStack remainder = ItemStack.EMPTY;

            for (int i = 0; i < availableSlots.size(); ++i) {
                int slot = availableSlots.get(i);

                // .copy() is mandatory!
                remainder = dest.insertItem(slot, current.copy(), action == Action.SIMULATE);

                // If we inserted *something*
                if (remainder.isEmpty() || current.getCount() != remainder.getCount()) {
                    availableSlots.remove(i);
                    break;
                }
            }

            if (remainder.isEmpty()) { // If we inserted successfully, get a next stack.
                current = stacks.poll();
            } else if (current.getCount() == remainder.getCount()) { // If we didn't insert anything over ALL these slots, stop here.
                break;
            } else { // If we didn't insert all, continue with other slots and use our remainder.
                current = remainder;
            }
        }

        return current == null && stacks.isEmpty();
    }

    @Override
    public int getCompletionPercentage() {
        if (totalSteps == 0) {
            return 0;
        }

        return 100 - (int) (((float) (crafting.size() + processing.size()) / (float) totalSteps) * 100F);
    }

    @Override
    public boolean update() {
        if (hasMissing()) {
            LOGGER.warn("Crafting task with missing items or fluids cannot execute, cancelling...");

            return true;
        }

        if (executionStarted == -1) {
            executionStarted = System.currentTimeMillis();

            totalSteps = crafting.size() + processing.size();
        }

        ++ticks;

        extractInitial();

        if (this.crafting.isEmpty() && this.processing.isEmpty()) {
            List<Runnable> toPerform = new ArrayList<>();

            for (ItemStack stack : internalStorage.getStacks()) {
                ItemStack remainder = network.insertItem(stack, stack.getCount(), Action.PERFORM);

                toPerform.add(() -> {
                    if (remainder == null) {
                        internalStorage.extract(stack, stack.getCount(), IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, Action.PERFORM);
                    } else {
                        internalStorage.extract(stack, stack.getCount() - remainder.getCount(), IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, Action.PERFORM);
                    }
                });
            }

            for (FluidStack stack : internalFluidStorage.getStacks()) {
                FluidStack remainder = network.insertFluid(stack, stack.amount, Action.PERFORM);

                toPerform.add(() -> {
                    if (remainder == null) {
                        internalFluidStorage.extract(stack, stack.amount, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, Action.PERFORM);
                    } else {
                        internalFluidStorage.extract(stack, stack.amount - remainder.amount, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT, Action.PERFORM);
                    }
                });
            }

            // Prevent CME.
            toPerform.forEach(Runnable::run);

            return internalStorage.getStacks().isEmpty() && internalFluidStorage.getStacks().isEmpty();
        } else {
            updateCrafting();
            updateProcessing();

            return false;
        }
    }

    @Override
    public void onCancelled() {
        for (ItemStack remainder : internalStorage.getStacks()) {
            network.insertItem(remainder, remainder.getCount(), Action.PERFORM);
        }

        for (FluidStack remainder : internalFluidStorage.getStacks()) {
            network.insertFluid(remainder, remainder.amount, Action.PERFORM);
        }
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public int getQuantityPerCraft() {
        int qty = 0;

        if (requested.getItem() != null) {
            for (ItemStack output : pattern.getOutputs()) {
                if (API.instance().getComparer().isEqualNoQuantity(output, requested.getItem())) {
                    qty += output.getCount();

                    if (!pattern.isProcessing()) {
                        break;
                    }
                }
            }
        } else {
            for (FluidStack output : pattern.getFluidOutputs()) {
                if (API.instance().getComparer().isEqual(output, requested.getFluid(), IComparer.COMPARE_NBT)) {
                    qty += output.amount;
                }
            }
        }

        return qty;
    }

    @Override
    public ICraftingRequestInfo getRequested() {
        return requested;
    }

    @Override
    public int onTrackedInsert(ItemStack stack, int size) {
        for (Processing p : processing.values()) {
            ItemStack content = p.getItemsToReceiveTotal().get(stack);

            if (content != null) {
                int needed = content.getCount();

                if (needed > size) {
                    needed = size;
                }

                if (p.calculateFinished(stack, needed)) {
                    p.setState(ProcessingState.PROCESSED);
                }

                size -= needed;

                if (!p.isRoot()) {
                    internalStorage.insert(stack, needed, Action.PERFORM);
                } else {
                    ItemStack remainder = network.insertItem(stack, needed, Action.PERFORM);

                    if (remainder != null) {
                        internalStorage.insert(stack, needed, Action.PERFORM);
                    }
                }

                if (size == 0) {
                    return 0;
                }
            }
        }

        return size;
    }

    @Override
    public int onTrackedInsert(FluidStack stack, int size) {
        for (Processing p : processing.values()) {

            FluidStack content = p.getFluidsToReceiveTotal().get(stack);

            if (content != null) {
                int needed = content.amount;

                if (needed > size) {
                    needed = size;
                }

                if (p.calculateFinished(stack, needed)) {
                    p.setState(ProcessingState.PROCESSED);
                }

                size -= needed;


                if (!p.isRoot()) {
                    internalFluidStorage.insert(stack, needed, Action.PERFORM);
                } else {
                    FluidStack remainder = network.insertFluid(stack, needed, Action.PERFORM);

                    if (remainder != null) {
                        internalFluidStorage.insert(stack, needed, Action.PERFORM);
                    }
                }

                if (size == 0) {
                    return 0;
                }
            }
        }

        return size;
    }

    static NBTTagCompound writePatternToNbt(ICraftingPattern pattern) {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag(NBT_PATTERN_STACK, pattern.getStack().serializeNBT());
        tag.setLong(NBT_PATTERN_CONTAINER_POS, pattern.getContainer().getPosition().toLong());

        return tag;
    }

    static ICraftingPattern readPatternFromNbt(NBTTagCompound tag, World world) throws CraftingTaskReadException {
        BlockPos containerPos = BlockPos.fromLong(tag.getLong(NBT_PATTERN_CONTAINER_POS));

        INetworkNode node = API.instance().getNetworkNodeManager(world).getNode(containerPos);

        if (node instanceof ICraftingPatternContainer) {
            ItemStack stack = new ItemStack(tag.getCompoundTag(NBT_PATTERN_STACK));

            if (stack.getItem() instanceof ICraftingPatternProvider) {
                return ((ICraftingPatternProvider) stack.getItem()).create(world, stack, (ICraftingPatternContainer) node);
            } else {
                throw new CraftingTaskReadException("Pattern stack is not a crafting pattern provider");
            }
        } else {
            throw new CraftingTaskReadException("Crafting pattern container doesn't exist anymore");
        }
    }

    @Override
    public List<ICraftingMonitorElement> getCraftingMonitorElements() {
        ICraftingMonitorElementList elements = API.instance().createCraftingMonitorElementList();

        for (ItemStack stack : this.internalStorage.getStacks()) {
            elements.add(new CraftingMonitorElementItemRender(stack, stack.getCount(), 0, 0, 0, 0));
        }

        for (ItemStack missing : this.missing.getStacks()) {
            elements.add(new CraftingMonitorElementItemRender(missing, 0, missing.getCount(), 0, 0, 0));
        }

        for (Crafting crafting : this.crafting) {
            for (ItemStack receive : crafting.getPattern().getOutputs()) {
                elements.add(new CraftingMonitorElementItemRender(receive, 0, 0, 0, 0, receive.getCount()));
            }
        }

        for (Processing processing : this.processing.values()) {
            if (processing.getState() == ProcessingState.PROCESSED) {
                continue;
            }

            if (processing.getState() == ProcessingState.READY_OR_PROCESSING || processing.getState() == ProcessingState.MACHINE_DOES_NOT_ACCEPT || processing.getState() == ProcessingState.MACHINE_NONE || processing.getState() == ProcessingState.LOCKED) {
                for (ItemStack p : processing.getItemsToPut().getStacks()) {
                    int proc = processing.getProcessing(p);
                    if (proc != 0) {
                        ICraftingMonitorElement element = new CraftingMonitorElementItemRender(p, 0, 0, proc, 0, 0);
                        elements.add(element);
                    }

                }
                for (ItemStack receive : processing.getItemsToReceiveTotal().getStacks()) {

                    int scheduled = processing.getScheduled(receive);
                    if (scheduled != 0) {
                        ICraftingMonitorElement element = new CraftingMonitorElementItemRender(receive, 0, 0, 0, scheduled, 0);
                        if (processing.getState() == ProcessingState.MACHINE_DOES_NOT_ACCEPT) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_does_not_accept_item");
                        } else if (processing.getState() == ProcessingState.MACHINE_NONE) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_none");
                        } else if (processing.getState() == ProcessingState.LOCKED) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.crafter_is_locked");
                        }

                        elements.add(element);
                    }
                }
            }
        }

        elements.commit();

        for (FluidStack stack : this.internalFluidStorage.getStacks()) {
            elements.add(new CraftingMonitorElementFluidRender(stack, stack.amount, 0, 0, 0, 0));
        }

        for (FluidStack missing : this.missingFluids.getStacks()) {
            elements.add(new CraftingMonitorElementFluidRender(missing, 0, missing.amount, 0, 0, 0));
        }

        for (Processing processing : this.processing.values()) {
            if (processing.getState() == ProcessingState.PROCESSED) {
                continue;
            }

            if (processing.getState() == ProcessingState.READY_OR_PROCESSING || processing.getState() == ProcessingState.MACHINE_DOES_NOT_ACCEPT || processing.getState() == ProcessingState.MACHINE_NONE) {
                for (FluidStack p : processing.getFluidsToPut().getStacks()) {
                    int proc = processing.getProcessing(p);
                    if (proc != 0) {
                        ICraftingMonitorElement element = new CraftingMonitorElementFluidRender(p, 0, 0, proc, 0, 0);
                        elements.add(element);
                    }

                }

                for (FluidStack receive : processing.getFluidsToPut().getStacks()) {
                    int scheduled = processing.getScheduled(receive);
                    if (scheduled != 0) {
                        ICraftingMonitorElement element = new CraftingMonitorElementFluidRender(receive, 0, 0, 0, scheduled, 0);

                        if (processing.getState() == ProcessingState.MACHINE_DOES_NOT_ACCEPT) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_does_not_accept_fluid");
                        } else if (processing.getState() == ProcessingState.MACHINE_NONE) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_none");
                        }

                        elements.add(element);
                    }
                }
            }
        }

        elements.commit();

        return elements.getElements();
    }

    @Override
    public List<ICraftingPreviewElement> getPreviewStacks() {
        Map<Integer, CraftingPreviewElementItemStack> map = new LinkedHashMap<>();
        Map<Integer, CraftingPreviewElementFluidStack> mapFluids = new LinkedHashMap<>();

        for (ItemStack stack : toCraft.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack);

            CraftingPreviewElementItemStack previewStack = map.get(hash);

            if (previewStack == null) {
                previewStack = new CraftingPreviewElementItemStack(stack);
            }

            previewStack.addToCraft(stack.getCount());

            map.put(hash, previewStack);
        }

        for (FluidStack stack : toCraftFluids.getStacks()) {
            int hash = API.instance().getFluidStackHashCode(stack);

            CraftingPreviewElementFluidStack previewStack = mapFluids.get(hash);

            if (previewStack == null) {
                previewStack = new CraftingPreviewElementFluidStack(stack);
            }

            previewStack.addToCraft(stack.amount);

            mapFluids.put(hash, previewStack);
        }

        for (ItemStack stack : missing.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack);

            CraftingPreviewElementItemStack previewStack = map.get(hash);

            if (previewStack == null) {
                previewStack = new CraftingPreviewElementItemStack(stack);
            }

            previewStack.setMissing(true);
            previewStack.addToCraft(stack.getCount());

            map.put(hash, previewStack);
        }

        for (FluidStack stack : missingFluids.getStacks()) {
            int hash = API.instance().getFluidStackHashCode(stack);

            CraftingPreviewElementFluidStack previewStack = mapFluids.get(hash);

            if (previewStack == null) {
                previewStack = new CraftingPreviewElementFluidStack(stack);
            }

            previewStack.setMissing(true);
            previewStack.addToCraft(stack.amount);

            mapFluids.put(hash, previewStack);
        }

        for (ItemStack stack : toTake.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack);

            CraftingPreviewElementItemStack previewStack = map.get(hash);

            if (previewStack == null) {
                previewStack = new CraftingPreviewElementItemStack(stack);
            }

            previewStack.addAvailable(stack.getCount());

            map.put(hash, previewStack);
        }

        for (FluidStack stack : toTakeFluids.getStacks()) {
            int hash = API.instance().getFluidStackHashCode(stack);

            CraftingPreviewElementFluidStack previewStack = mapFluids.get(hash);

            if (previewStack == null) {
                previewStack = new CraftingPreviewElementFluidStack(stack);
            }

            previewStack.addAvailable(stack.amount);

            mapFluids.put(hash, previewStack);
        }

        List<ICraftingPreviewElement> elements = new ArrayList<>();

        elements.addAll(map.values());
        elements.addAll(mapFluids.values());

        return elements;
    }

    @Override
    public ICraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public long getExecutionStarted() {
        return executionStarted;
    }

    @Override
    public IStackList<ItemStack> getMissing() {
        return missing;
    }

    @Override
    public IStackList<FluidStack> getMissingFluids() {
        return missingFluids;
    }

    @Override
    public UUID getId() {
        return id;
    }
}
