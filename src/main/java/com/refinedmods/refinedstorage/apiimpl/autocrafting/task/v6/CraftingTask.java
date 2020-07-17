package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.autocrafting.task.*;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.monitor.CraftingMonitorElementFactory;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.CraftingTaskNode;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.CraftingTaskNodeList;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.ProcessingCraftingTaskNode;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.RecipeCraftingTaskNode;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.preview.CraftingPreviewElementFactory;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.FluidStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.ItemStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.FluidStorageDiskFactory;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.ItemStorageDiskFactory;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final String NBT_CRAFTS = "Crafts";
    private static final String NBT_MISSING = "Missing";
    private static final String NBT_MISSING_FLUIDS = "MissingFluids";
    private static final String NBT_TOTAL_STEPS = "TotalSteps";
    private static final String NBT_CURRENT_STEP = "CurrentStep";

    private static final Logger LOGGER = LogManager.getLogger(CraftingTask.class);

    private final INetwork network;
    private final ICraftingRequestInfo requested;
    private final int quantity;
    private final ICraftingPattern pattern;
    private UUID id = UUID.randomUUID();
    private int ticks;
    private long calculationStarted = -1;
    private long executionStarted = -1;
    private int totalSteps;
    private int currentStep;
    private final Set<ICraftingPattern> patternsUsed = new HashSet<>();

    private final IStorageDisk<ItemStack> internalStorage;
    private final IStorageDisk<FluidStack> internalFluidStorage;

    private IStackList<ItemStack> toExtractInitial = API.instance().createItemStackList();
    private IStackList<FluidStack> toExtractInitialFluids = API.instance().createFluidStackList();

    private final CraftingTaskNodeList nodes = new CraftingTaskNodeList();

    private IStackList<ItemStack> missing = API.instance().createItemStackList();
    private IStackList<FluidStack> missingFluids = API.instance().createFluidStackList();

    private final IStackList<ItemStack> toTake = API.instance().createItemStackList();
    private final IStackList<FluidStack> toTakeFluids = API.instance().createFluidStackList();

    private final List<ItemStack> toCraft = new ArrayList<>();
    private final List<FluidStack> toCraftFluids = new ArrayList<>();

    private final CraftingPreviewElementFactory craftingPreviewElementFactory = new CraftingPreviewElementFactory(this);
    private final CraftingMonitorElementFactory craftingMonitorElementFactory = new CraftingMonitorElementFactory();

    public CraftingTask(INetwork network, ICraftingRequestInfo requested, int quantity, ICraftingPattern pattern) {
        this.network = network;
        this.requested = requested;
        this.quantity = quantity;
        this.pattern = pattern;

        this.internalStorage = new ItemStorageDisk(null, -1);
        this.internalFluidStorage = new FluidStorageDisk(null, -1);
    }

    public CraftingTask(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        this.network = network;

        this.requested = API.instance().createCraftingRequestInfo(tag.getCompound(NBT_REQUESTED));
        this.quantity = tag.getInt(NBT_QUANTITY);
        this.pattern = SerializationUtil.readPatternFromNbt(tag.getCompound(NBT_PATTERN), network.getWorld());
        this.ticks = tag.getInt(NBT_TICKS);
        this.id = tag.getUniqueId(NBT_ID);
        this.executionStarted = tag.getLong(NBT_EXECUTION_STARTED);
        this.totalSteps = tag.getInt(NBT_TOTAL_STEPS);
        this.currentStep = tag.getInt(NBT_CURRENT_STEP);

        ItemStorageDiskFactory factoryItem = new ItemStorageDiskFactory();
        FluidStorageDiskFactory factoryFluid = new FluidStorageDiskFactory();

        this.internalStorage = factoryItem.createFromNbt(null, tag.getCompound(NBT_INTERNAL_STORAGE));
        this.internalFluidStorage = factoryFluid.createFromNbt(null, tag.getCompound(NBT_INTERNAL_FLUID_STORAGE));

        this.toExtractInitial = SerializationUtil.readItemStackList(tag.getList(NBT_TO_EXTRACT_INITIAL, Constants.NBT.TAG_COMPOUND));
        this.toExtractInitialFluids = SerializationUtil.readFluidStackList(tag.getList(NBT_TO_EXTRACT_INITIAL_FLUIDS, Constants.NBT.TAG_COMPOUND));

        ListNBT craftList = tag.getList(NBT_CRAFTS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < craftList.size(); ++i) {
            CraftingTaskNode c = CraftingTaskNode.createCraftFromNBT(network, craftList.getCompound(i));
            nodes.put(c.getPattern(), c);
        }

        this.missing = SerializationUtil.readItemStackList(tag.getList(NBT_MISSING, Constants.NBT.TAG_COMPOUND));
        this.missingFluids = SerializationUtil.readFluidStackList(tag.getList(NBT_MISSING_FLUIDS, Constants.NBT.TAG_COMPOUND));
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.put(NBT_REQUESTED, requested.writeToNbt());
        tag.putInt(NBT_QUANTITY, quantity);
        tag.put(NBT_PATTERN, SerializationUtil.writePatternToNbt(pattern));
        tag.putInt(NBT_TICKS, ticks);
        tag.putUniqueId(NBT_ID, id);
        tag.putLong(NBT_EXECUTION_STARTED, executionStarted);
        tag.put(NBT_INTERNAL_STORAGE, internalStorage.writeToNbt());
        tag.put(NBT_INTERNAL_FLUID_STORAGE, internalFluidStorage.writeToNbt());
        tag.put(NBT_TO_EXTRACT_INITIAL, SerializationUtil.writeItemStackList(toExtractInitial));
        tag.put(NBT_TO_EXTRACT_INITIAL_FLUIDS, SerializationUtil.writeFluidStackList(toExtractInitialFluids));
        tag.putInt(NBT_TOTAL_STEPS, totalSteps);
        tag.putInt(NBT_CURRENT_STEP, currentStep);

        ListNBT craftingList = new ListNBT();
        for (CraftingTaskNode craft : this.nodes.all()) {
            craftingList.add(craft.writeToNbt());
        }
        tag.put(NBT_CRAFTS, craftingList);

        tag.put(NBT_MISSING, SerializationUtil.writeItemStackList(missing));
        tag.put(NBT_MISSING_FLUIDS, SerializationUtil.writeFluidStackList(missingFluids));

        return tag;
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

        IStackList<ItemStack> results = API.instance().createItemStackList();
        IStackList<FluidStack> fluidResults = API.instance().createFluidStackList();

        IStackList<ItemStack> storageSource = network.getItemStorageCache().getList().copy();
        IStackList<FluidStack> fluidStorageSource = network.getFluidStorageCache().getList().copy();

        int qtyPerCraft = getQuantityPerCraft(requested.getItem(), requested.getFluid(), this.pattern);
        int qty = ((this.quantity - 1) / qtyPerCraft) + 1;

        ICraftingTaskError error = calculateInternal(qty, storageSource, fluidStorageSource, results, fluidResults, this.pattern, true);
        if (error != null) {
            return error;
        }

        if (requested.getItem() != null) {
            this.toCraft.add(ItemHandlerHelper.copyStackWithSize(requested.getItem(), qty * qtyPerCraft));
        } else if (requested.getFluid() != null) {
            this.toCraftFluids.add(StackUtils.copy(requested.getFluid(), qty * qtyPerCraft));
        }

        return null;
    }

    @Nullable
    private ICraftingTaskError calculateInternal(
        int qty,
        IStackList<ItemStack> storageSource,
        IStackList<FluidStack> fluidStorageSource,
        IStackList<ItemStack> results,
        IStackList<FluidStack> fluidResults,
        ICraftingPattern pattern,
        boolean root) {

        if (System.currentTimeMillis() - calculationStarted > RS.SERVER_CONFIG.getAutocrafting().getCalculationTimeoutMs()) {
            return new CraftingTaskError(CraftingTaskErrorType.TOO_COMPLEX);
        }

        if (!patternsUsed.add(pattern)) {
            return new CraftingTaskError(CraftingTaskErrorType.RECURSIVE, pattern);
        }

        IStackList<ItemStack> itemsToExtract = API.instance().createItemStackList();
        IStackList<FluidStack> fluidsToExtract = API.instance().createFluidStackList();

        NonNullList<ItemStack> recipe = NonNullList.create();
        List<Pair<NonNullList<ItemStack>, Integer>> ingredients = new ArrayList<>();

        combineCommonStacks(recipe, ingredients, pattern);

        CraftingTaskNode node = nodes.createOrAddToExistingNode(pattern, root, recipe, qty);

        int ingredientNumber = -1;

        for (Pair<NonNullList<ItemStack>, Integer> pair : ingredients) {
            ingredientNumber++;

            PossibleInputs possibleInputs = new PossibleInputs(new ArrayList<>(pair.getLeft()));
            possibleInputs.sort(storageSource, results);

            ItemStack possibleInput = possibleInputs.get();

            ItemStack fromSelf = results.get(possibleInput);
            ItemStack fromNetwork = storageSource.get(possibleInput);

            int remaining = pair.getRight() * qty;

            if (remaining < 0) { //int overflow
                return new CraftingTaskError(CraftingTaskErrorType.TOO_COMPLEX);
            }

            while (remaining > 0) {
                if (fromSelf != null) {
                    int toTake = Math.min(remaining, fromSelf.getCount());

                    node.addItemsToUse(ingredientNumber, possibleInput, toTake, pair.getRight());

                    results.remove(fromSelf, toTake);

                    remaining -= toTake;

                    fromSelf = results.get(possibleInput);
                }
                if (fromNetwork != null && remaining > 0) {
                    int toTake = Math.min(remaining, fromNetwork.getCount());

                    this.toTake.add(possibleInput, toTake);

                    node.addItemsToUse(ingredientNumber, possibleInput, toTake, pair.getRight());

                    storageSource.remove(fromNetwork, toTake);

                    remaining -= toTake;

                    fromNetwork = storageSource.get(possibleInput);

                    toExtractInitial.add(possibleInput, toTake);
                }
                if (remaining > 0) {
                    ICraftingPattern subPattern = network.getCraftingManager().getPattern(possibleInput);

                    if (subPattern != null) {
                        int qtyPerCraft = getQuantityPerCraft(possibleInput, null, subPattern);
                        int subQty = ((remaining - 1) / qtyPerCraft) + 1; //CeilDiv

                        ICraftingTaskError result = calculateInternal(subQty, storageSource, fluidStorageSource, results, fluidResults, subPattern, false);

                        if (result != null) {
                            return result;
                        }

                        fromSelf = results.get(possibleInput);
                        if (fromSelf == null) {
                            throw new IllegalStateException("Recursive calculation didn't yield anything");
                        }

                        fromNetwork = storageSource.get(possibleInput);
                        // fromSelf contains the amount crafted after the loop.
                        this.toCraft.add(fromSelf.copy());


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
                            fromNetwork = storageSource.get(possibleInput);
                        }
                    }
                }
            }
        }

        if (node instanceof RecipeCraftingTaskNode) {
            ItemStack output = pattern.getOutput(recipe);
            results.add(output, output.getCount() * qty);

            for (ItemStack byproduct : pattern.getByproducts(recipe)) {
                results.add(byproduct, byproduct.getCount() * qty);
            }
        } else {
            ProcessingCraftingTaskNode processing = (ProcessingCraftingTaskNode) node;

            ingredientNumber = -1;

            for (NonNullList<FluidStack> inputs : pattern.getFluidInputs()) {
                if (inputs.isEmpty()) {
                    continue;
                }
                ingredientNumber++;

                PossibleFluidInputs possibleInputs = new PossibleFluidInputs(new ArrayList<>(inputs));
                possibleInputs.sort(fluidStorageSource, fluidResults);

                FluidStack possibleInput = possibleInputs.get();

                FluidStack fromSelf = fluidResults.get(possibleInput, IComparer.COMPARE_NBT);
                FluidStack fromNetwork = fluidStorageSource.get(possibleInput, IComparer.COMPARE_NBT);

                int remaining = possibleInput.getAmount() * qty;

                if (remaining < 0) { //int overflow
                    return new CraftingTaskError(CraftingTaskErrorType.TOO_COMPLEX);
                }

                processing.addFluidsToUse(possibleInput);

                while (remaining > 0) {
                    if (fromSelf != null) {
                        int toTake = Math.min(remaining, fromSelf.getAmount());

                        fluidResults.remove(possibleInput, toTake);

                        remaining -= toTake;

                        fromSelf = fluidResults.get(possibleInput, IComparer.COMPARE_NBT);
                    }
                    if (fromNetwork != null && remaining > 0) {

                        int toTake = Math.min(remaining, fromNetwork.getAmount());

                        this.toTakeFluids.add(possibleInput, toTake);

                        fluidStorageSource.remove(fromNetwork, toTake);

                        remaining -= toTake;

                        fromNetwork = fluidStorageSource.get(possibleInput, IComparer.COMPARE_NBT);

                        toExtractInitialFluids.add(possibleInput, toTake);
                    }
                    if (remaining > 0) {
                        ICraftingPattern subPattern = network.getCraftingManager().getPattern(possibleInput);

                        if (subPattern != null) {
                            int qtyPerCraft = getQuantityPerCraft(null, possibleInput, subPattern);
                            int subQty = ((remaining - 1) / qtyPerCraft) + 1; //CeilDiv

                            ICraftingTaskError result = calculateInternal(subQty, storageSource, fluidStorageSource, results, fluidResults, subPattern, false);

                            if (result != null) {
                                return result;
                            }

                            fromSelf = fluidResults.get(possibleInput, IComparer.COMPARE_NBT);
                            if (fromSelf == null) {
                                throw new IllegalStateException("Recursive fluid calculation didn't yield anything");
                            }

                            fromNetwork = fluidStorageSource.get(possibleInput, IComparer.COMPARE_NBT);

                            // fromSelf contains the amount crafted after the loop.
                            this.toCraftFluids.add(fromSelf.copy());
                        } else {
                            if (!possibleInputs.cycle()) {
                                // Give up.
                                possibleInput = possibleInputs.get(); // Revert back to 0.

                                this.missingFluids.add(possibleInput, remaining);

                                fluidsToExtract.add(possibleInput, remaining);

                                remaining = 0;
                            } else {
                                // Retry with new input...
                                possibleInput = possibleInputs.get();

                                fromSelf = fluidResults.get(possibleInput);
                                fromNetwork = fluidStorageSource.get(possibleInput);
                            }
                        }
                    }
                }
            }

            pattern.getOutputs().forEach(x -> results.add(x, x.getCount() * qty));
            pattern.getFluidOutputs().forEach(x -> fluidResults.add(x, x.getAmount() * qty));

            //only add this once
            if (processing.getItemsToReceive().isEmpty()) {
                pattern.getOutputs().forEach(processing::addItemsToReceive);
            }

            if (processing.getFluidsToReceive().isEmpty()) {
                pattern.getFluidOutputs().forEach(processing::addFluidsToReceive);
            }
        }

        patternsUsed.remove(pattern);
        return null;
    }

    @Override
    public void start() {
        if (hasMissing()) {
            LOGGER.warn("Crafting task with missing items or fluids cannot execute, cancelling...");
            return;
        }

        nodes.all().forEach(node -> {
            totalSteps += node.getQuantity();
            node.finishCalculation();
        });

        executionStarted = System.currentTimeMillis();

        extractInitial();
    }

    private void extractInitial() {
        if (!toExtractInitial.isEmpty()) {
            List<ItemStack> toRemove = new ArrayList<>();

            for (StackListEntry<ItemStack> toExtract : toExtractInitial.getStacks()) {
                ItemStack result = network.extractItem(toExtract.getStack(), toExtract.getStack().getCount(), Action.PERFORM);

                if (!result.isEmpty()) {
                    internalStorage.insert(toExtract.getStack(), result.getCount(), Action.PERFORM);

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

            for (StackListEntry<FluidStack> toExtract : toExtractInitialFluids.getStacks()) {
                FluidStack result = network.extractFluid(toExtract.getStack(), toExtract.getStack().getAmount(), Action.PERFORM);

                if (!result.isEmpty()) {
                    internalFluidStorage.insert(toExtract.getStack(), result.getAmount(), Action.PERFORM);

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

    private void combineCommonStacks(NonNullList<ItemStack> recipe, List<Pair<NonNullList<ItemStack>, Integer>> ingredients, ICraftingPattern pattern) {
        for (NonNullList<ItemStack> inputs : pattern.getInputs()) {
            if (inputs.isEmpty()) {
                recipe.add(ItemStack.EMPTY);
            } else {
                recipe.add(inputs.get(0));

                boolean match = false;
                for (Pair<NonNullList<ItemStack>, Integer> pair : ingredients) {
                    if (pair.getLeft().size() == inputs.size()) {
                        match = true;
                        for (int i = 0; i < inputs.size(); i++) {
                            if (!API.instance().getComparer().isEqualNoQuantity(pair.getLeft().get(i), inputs.get(i))) {
                                match = false;
                                break;
                            }
                        }
                        if (match) {
                            pair.setValue(pair.getRight() + inputs.get(0).getCount());
                            break;
                        }
                    }
                }
                if (!match) {
                    ingredients.add(new MutablePair<>(inputs, inputs.get(0).getCount()));
                }
            }
        }
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
        if (hasMissing()) {
            LOGGER.warn("Crafting task with missing items or fluids cannot execute, cancelling...");

            return true;
        }

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
            extractInitial();

            for (CraftingTaskNode node : nodes.all()) {
                node.update(network, ticks, nodes, internalStorage, internalFluidStorage);
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

    public int getQuantityPerCraft(ItemStack item, FluidStack fluid, ICraftingPattern pattern) {
        int qty = 0;

        if (item != null) {
            for (ItemStack output : pattern.getOutputs()) {
                if (API.instance().getComparer().isEqualNoQuantity(output, item)) {
                    qty += output.getCount();

                    if (!pattern.isProcessing()) {
                        break;
                    }
                }
            }
        } else {
            for (FluidStack output : pattern.getFluidOutputs()) {
                if (API.instance().getComparer().isEqual(output, fluid, IComparer.COMPARE_NBT)) {
                    qty += output.getAmount();
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
        for (CraftingTaskNode craft : this.nodes.all()) {
            if (craft instanceof ProcessingCraftingTaskNode) {
                ProcessingCraftingTaskNode p = (ProcessingCraftingTaskNode) craft;

                int needed = p.getNeeded(stack);
                if (needed > 0) {

                    if (needed > size) {
                        needed = size;
                    }

                    p.addFinished(stack, needed);

                    size -= needed;

                    if (!p.isRoot()) {
                        internalStorage.insert(stack, needed, Action.PERFORM);
                    } else {
                        ItemStack remainder = network.insertItem(stack, needed, Action.PERFORM);

                        internalStorage.insert(remainder, remainder.getCount(), Action.PERFORM);
                    }

                    if (p.updateFinished()) { //only update if finished changes
                        network.getCraftingManager().onTaskChanged();
                    }

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
        for (CraftingTaskNode craft : this.nodes.all()) {
            if (craft instanceof ProcessingCraftingTaskNode) {
                ProcessingCraftingTaskNode p = (ProcessingCraftingTaskNode) craft;

                int needed = p.getNeeded(stack);

                if (needed > 0) {

                    if (needed > size) {
                        needed = size;
                    }

                    p.addFinished(stack, needed);

                    size -= needed;

                    if (!p.isRoot()) {
                        internalFluidStorage.insert(stack, needed, Action.PERFORM);
                    } else {
                        FluidStack remainder = network.insertFluid(stack, needed, Action.PERFORM);

                        internalFluidStorage.insert(remainder, remainder.getAmount(), Action.PERFORM);
                    }

                    if (p.updateFinished()) { //only update if finished changees
                        network.getCraftingManager().onTaskChanged();
                    }

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
        return craftingMonitorElementFactory.getList(nodes.all(), internalStorage, internalFluidStorage);
    }

    @Override
    public List<ICraftingPreviewElement<?>> getPreviewElements() {
        return craftingPreviewElementFactory.getElements(toCraft, toCraftFluids, toTake, toTakeFluids);
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
