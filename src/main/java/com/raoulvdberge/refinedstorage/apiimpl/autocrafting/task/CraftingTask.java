package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

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
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.*;

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

    private static final String NBT_PATTERN_STACK = "Stack";
    private static final String NBT_PATTERN_CONTAINER_POS = "ContainerPos";

    private static final long CALCULATION_TIMEOUT_MS = 5000;

    private INetwork network;
    private ICraftingRequestInfo requested;
    private int quantity;
    private ICraftingPattern pattern;
    private UUID id = UUID.randomUUID();
    private int ticks;
    private long calculationStarted = -1;
    private long executionStarted = -1;
    private Set<ICraftingPattern> patternsUsed = new HashSet<>();

    private IStorageDisk<ItemStack> internalStorage;
    private IStorageDisk<FluidStack> internalFluidStorage;

    private IStackList<ItemStack> toExtractInitial = API.instance().createItemStackList();
    private IStackList<FluidStack> toExtractInitialFluids = API.instance().createFluidStackList();

    private List<Crafting> crafting = new ArrayList<>();
    private List<Processing> processing = new ArrayList<>();

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
            processing.add(new Processing(network, processingList.getCompoundTagAt(i)));
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

        NBTTagList craftingList = new NBTTagList();
        for (Crafting crafting : this.crafting) {
            craftingList.appendTag(crafting.writeToNbt());
        }

        tag.setTag(NBT_CRAFTING, craftingList);

        NBTTagList processingList = new NBTTagList();
        for (Processing processing : this.processing) {
            processingList.appendTag(processing.writeToNbt());
        }

        tag.setTag(NBT_PROCESSING, processingList);

        tag.setTag(NBT_MISSING, writeItemStackList(missing));
        tag.setTag(NBT_MISSING_FLUIDS, writeFluidStackList(missingFluids));

        return tag;
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

        if (requested.getItem() != null) {
            this.toCraft.add(requested.getItem(), crafted);
        } else {
            this.toCraftFluids.add(requested.getFluid(), crafted);
        }

        return null;
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

        if (System.currentTimeMillis() - calculationStarted > CALCULATION_TIMEOUT_MS) {
            return new CraftingTaskError(CraftingTaskErrorType.TOO_COMPLEX);
        }

        if (!patternsUsed.add(pattern)) {
            return new CraftingTaskError(CraftingTaskErrorType.RECURSIVE, pattern);
        }

        IStackList<ItemStack> itemsToExtract = API.instance().createItemStackList();
        IStackList<FluidStack> fluidsToExtract = API.instance().createFluidStackList();

        NonNullList<ItemStack> took = NonNullList.create();

        for (NonNullList<ItemStack> possibleInputs : pattern.getInputs()) {
            if (possibleInputs.isEmpty()) {
                took.add(ItemStack.EMPTY);

                continue;
            }

            ItemStack possibleInput;

            if (possibleInputs.size() == 1) {
                possibleInput = possibleInputs.get(0);
            } else {
                NonNullList<ItemStack> sortedPossibleInputs = NonNullList.create();
                sortedPossibleInputs.addAll(possibleInputs);

                sortedPossibleInputs.sort((a, b) -> {
                    ItemStack ar = mutatedStorage.get(a);
                    ItemStack br = mutatedStorage.get(b);

                    return (br == null ? 0 : br.getCount()) - (ar == null ? 0 : ar.getCount());
                });

                sortedPossibleInputs.sort((a, b) -> {
                    ItemStack ar = results.get(a);
                    ItemStack br = results.get(b);

                    return (br == null ? 0 : br.getCount()) - (ar == null ? 0 : ar.getCount());
                });

                possibleInput = sortedPossibleInputs.get(0);
            }

            took.add(possibleInput);

            int flags = getFlags(possibleInput);

            ItemStack fromSelf = results.get(possibleInput, flags);
            ItemStack fromNetwork = mutatedStorage.get(possibleInput, flags);

            int remaining = possibleInput.getCount();

            while (remaining > 0) {
                if (fromSelf != null) {
                    int toTake = Math.min(remaining, fromSelf.getCount());

                    itemsToExtract.add(possibleInput, toTake);

                    results.remove(fromSelf, toTake);

                    remaining -= toTake;

                    took.set(took.size() - 1, ItemHandlerHelper.copyStackWithSize(fromSelf, possibleInput.getCount()));

                    fromSelf = results.get(possibleInput, flags);
                } else if (fromNetwork != null) {
                    int toTake = Math.min(remaining, fromNetwork.getCount());

                    this.toTake.add(possibleInput, toTake);

                    itemsToExtract.add(possibleInput, toTake);

                    mutatedStorage.remove(fromNetwork, toTake);

                    remaining -= toTake;

                    took.set(took.size() - 1, ItemHandlerHelper.copyStackWithSize(fromNetwork, possibleInput.getCount()));

                    fromNetwork = mutatedStorage.get(possibleInput, flags);

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

                            fromSelf = results.get(possibleInput, flags);
                            if (fromSelf == null) {
                                throw new IllegalStateException("Recursive calculation didn't yield anything");
                            }

                            fromNetwork = mutatedStorage.get(possibleInput, flags);

                            subPatternChain.cycle();
                        }

                        // fromSelf contains the amount crafted after the loop.
                        this.toCraft.add(possibleInput, fromSelf.getCount());
                    } else {
                        this.missing.add(possibleInput, remaining);

                        itemsToExtract.add(possibleInput, remaining);

                        remaining = 0;
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

            processing.add(new Processing(pattern, itemsToReceive, fluidsToReceive, new ArrayList<>(itemsToExtract.getStacks()), new ArrayList<>(fluidsToExtract.getStacks()), root));
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

    private static int getTickInterval(int speedUpgrades) {
        switch (speedUpgrades) {
            case 0:
                return 10;
            case 1:
                return 8;
            case 2:
                return 6;
            case 3:
                return 4;
            case 4:
                return 2;
            default:
                return 2;
        }
    }

    private void extractInitial() {
        if (!toExtractInitial.isEmpty()) {
            List<ItemStack> toRemove = new ArrayList<>();

            for (ItemStack toExtract : toExtractInitial.getStacks()) {
                ItemStack result = network.extractItem(toExtract, toExtract.getCount(), getFlags(toExtract), Action.PERFORM);

                if (result != null) {
                    internalStorage.insert(toExtract, toExtract.getCount(), Action.PERFORM);

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
                    internalFluidStorage.insert(toExtract, toExtract.amount, Action.PERFORM);

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

        while (it.hasNext()) {
            Crafting c = it.next();

            if (ticks % getTickInterval(c.getPattern().getContainer().getSpeedUpgradeCount()) == 0) {
                boolean hasAll = true;

                for (ItemStack need : c.getToExtract().getStacks()) {
                    ItemStack result = this.internalStorage.extract(need, need.getCount(), getFlags(need), Action.SIMULATE);

                    if (result == null || result.getCount() != need.getCount()) {
                        hasAll = false;

                        break;
                    }
                }

                if (hasAll) {
                    for (ItemStack need : c.getToExtract().getStacks()) {
                        ItemStack result = this.internalStorage.extract(need, need.getCount(), getFlags(need), Action.PERFORM);

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

                    return;
                }
            }
        }
    }

    private void updateProcessing() {
        Iterator<Processing> it = processing.iterator();

        while (it.hasNext()) {
            Processing p = it.next();

            if (p.getState() == ProcessingState.PROCESSED) {
                it.remove();

                network.getCraftingManager().onTaskChanged();

                continue;
            }

            if (p.getState() == ProcessingState.EXTRACTED_ALL) {
                continue;
            }

            if (ticks % getTickInterval(p.getPattern().getContainer().getSpeedUpgradeCount()) == 0) {
                ProcessingState originalState = p.getState();

                if (p.getPattern().getContainer().isLocked()) {
                    p.setState(ProcessingState.LOCKED);
                } else {
                    boolean hasAll = true;

                    for (ItemStack need : p.getItemsToPut()) {
                        if (p.getPattern().getContainer().getConnectedInventory() == null) {
                            p.setState(ProcessingState.MACHINE_NONE);
                        } else {
                            ItemStack result = this.internalStorage.extract(need, need.getCount(), getFlags(need), Action.SIMULATE);

                            if (result == null || result.getCount() != need.getCount()) {
                                hasAll = false;

                                break;
                            } else if (!ItemHandlerHelper.insertItem(p.getPattern().getContainer().getConnectedInventory(), result, true).isEmpty()) {
                                p.setState(ProcessingState.MACHINE_DOES_NOT_ACCEPT);

                                break;
                            } else {
                                p.setState(ProcessingState.READY);
                            }
                        }
                    }

                    for (FluidStack need : p.getFluidsToPut()) {
                        if (p.getPattern().getContainer().getConnectedFluidInventory() == null) {
                            p.setState(ProcessingState.MACHINE_NONE);
                        } else {
                            FluidStack result = this.internalFluidStorage.extract(need, need.amount, IComparer.COMPARE_NBT, Action.SIMULATE);

                            if (result == null || result.amount != need.amount) {
                                hasAll = false;

                                break;
                            } else if (p.getPattern().getContainer().getConnectedFluidInventory().fill(result, false) != result.amount) {
                                p.setState(ProcessingState.MACHINE_DOES_NOT_ACCEPT);

                                break;
                            } else if (p.getState() == ProcessingState.READY) { // If the items were ok.
                                p.setState(ProcessingState.READY);
                            }
                        }
                    }

                    if (p.getState() == ProcessingState.READY && hasAll) {
                        for (ItemStack need : p.getItemsToPut()) {
                            ItemStack result = this.internalStorage.extract(need, need.getCount(), getFlags(need), Action.PERFORM);
                            if (result == null || result.getCount() != need.getCount()) {
                                throw new IllegalStateException("Could not extract from the internal inventory even though we could");
                            }

                            if (!ItemHandlerHelper.insertItem(p.getPattern().getContainer().getConnectedInventory(), result, false).isEmpty()) {
                                throw new IllegalStateException("Can't fill up inventory even though we could");
                            }
                        }

                        for (FluidStack need : p.getFluidsToPut()) {
                            FluidStack result = this.internalFluidStorage.extract(need, need.amount, IComparer.COMPARE_NBT, Action.PERFORM);
                            if (result == null || result.amount != need.amount) {
                                throw new IllegalStateException("Could not extract from the internal inventory even though we could");
                            }

                            if (p.getPattern().getContainer().getConnectedFluidInventory().fill(result, true) != result.amount) {
                                throw new IllegalStateException("Can't fill up inventory even though we could");
                            }
                        }

                        p.setState(ProcessingState.EXTRACTED_ALL);

                        p.getPattern().getContainer().onUsedForProcessing();
                    }
                }

                if (originalState != p.getState()) {
                    network.getCraftingManager().onTaskChanged();
                }
            }
        }
    }

    @Override
    public boolean update() {
        if (executionStarted == -1) {
            executionStarted = System.currentTimeMillis();
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

    private static int getFlags(ItemStack stack) {
        if (stack.getItem().isDamageable()) {
            return IComparer.COMPARE_NBT;
        }

        return IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    }

    @Override
    public ICraftingRequestInfo getRequested() {
        return requested;
    }

    @Override
    public int onTrackedInsert(ItemStack stack, int size) {
        for (Processing p : this.processing) {
            if (p.getState() != ProcessingState.EXTRACTED_ALL) {
                continue;
            }

            ItemStack content = p.getItemsToReceive().get(stack);

            if (content != null) {
                int needed = content.getCount();

                if (needed > size) {
                    needed = size;
                }

                p.getItemsToReceive().remove(stack, needed);

                size -= needed;

                if (p.getItemsToReceive().isEmpty() && p.getFluidsToReceive().isEmpty()) {
                    p.setState(ProcessingState.PROCESSED);
                }

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
        for (Processing p : this.processing) {
            if (p.getState() != ProcessingState.EXTRACTED_ALL) {
                continue;
            }

            FluidStack content = p.getFluidsToReceive().get(stack);

            if (content != null) {
                int needed = content.amount;

                if (needed > size) {
                    needed = size;
                }

                p.getFluidsToReceive().remove(stack, needed);

                size -= needed;

                if (p.getItemsToReceive().isEmpty() && p.getFluidsToReceive().isEmpty()) {
                    p.setState(ProcessingState.PROCESSED);
                }

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

        for (Processing processing : this.processing) {
            if (processing.getState() == ProcessingState.PROCESSED) {
                continue;
            }

            if (processing.getState() == ProcessingState.EXTRACTED_ALL) {
                for (ItemStack put : processing.getItemsToPut()) {
                    elements.add(new CraftingMonitorElementItemRender(put, 0, 0, put.getCount(), 0, 0));
                }
            } else if (processing.getState() == ProcessingState.READY || processing.getState() == ProcessingState.MACHINE_DOES_NOT_ACCEPT || processing.getState() == ProcessingState.MACHINE_NONE || processing.getState() == ProcessingState.LOCKED) {
                for (ItemStack receive : processing.getItemsToReceive().getStacks()) {
                    ICraftingMonitorElement element = new CraftingMonitorElementItemRender(receive, 0, 0, 0, receive.getCount(), 0);

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

        elements.commit();

        for (FluidStack stack : this.internalFluidStorage.getStacks()) {
            elements.add(new CraftingMonitorElementFluidRender(stack, stack.amount, 0, 0, 0, 0));
        }

        for (FluidStack missing : this.missingFluids.getStacks()) {
            elements.add(new CraftingMonitorElementFluidRender(missing, 0, missing.amount, 0, 0, 0));
        }

        for (Processing processing : this.processing) {
            if (processing.getState() == ProcessingState.PROCESSED) {
                continue;
            }

            if (processing.getState() == ProcessingState.EXTRACTED_ALL) {
                for (FluidStack put : processing.getFluidsToPut()) {
                    elements.add(new CraftingMonitorElementFluidRender(put, 0, 0, put.amount, 0, 0));
                }
            } else if (processing.getState() == ProcessingState.READY || processing.getState() == ProcessingState.MACHINE_DOES_NOT_ACCEPT || processing.getState() == ProcessingState.MACHINE_NONE) {
                for (FluidStack receive : processing.getFluidsToReceive().getStacks()) {
                    ICraftingMonitorElement element = new CraftingMonitorElementFluidRender(receive, 0, 0, 0, receive.amount, 0);

                    if (processing.getState() == ProcessingState.MACHINE_DOES_NOT_ACCEPT) {
                        element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_does_not_accept_fluid");
                    } else if (processing.getState() == ProcessingState.MACHINE_NONE) {
                        element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_none");
                    }

                    elements.add(element);
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
    public UUID getId() {
        return id;
    }
}
