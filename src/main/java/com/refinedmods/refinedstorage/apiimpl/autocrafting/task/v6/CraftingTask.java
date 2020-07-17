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
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.monitor.CraftingMonitorElementFactory;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.CraftingTaskNode;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.CraftingTaskNodeList;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.ProcessingCraftingTaskNode;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node.RecipeCraftingTaskNode;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.preview.CraftingPreviewElementFactory;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.preview.CraftingPreviewInfo;
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
    private static final String NBT_TOTAL_STEPS = "TotalSteps";
    private static final String NBT_CURRENT_STEP = "CurrentStep";

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

    private final CraftingPreviewInfo craftingPreviewInfo = new CraftingPreviewInfo();

    private final CraftingPreviewElementFactory craftingPreviewElementFactory = new CraftingPreviewElementFactory();
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

        this.internalStorage = new ItemStorageDiskFactory().createFromNbt(null, tag.getCompound(NBT_INTERNAL_STORAGE));
        this.internalFluidStorage = new FluidStorageDiskFactory().createFromNbt(null, tag.getCompound(NBT_INTERNAL_FLUID_STORAGE));

        this.toExtractInitial = SerializationUtil.readItemStackList(tag.getList(NBT_TO_EXTRACT_INITIAL, Constants.NBT.TAG_COMPOUND));
        this.toExtractInitialFluids = SerializationUtil.readFluidStackList(tag.getList(NBT_TO_EXTRACT_INITIAL_FLUIDS, Constants.NBT.TAG_COMPOUND));

        ListNBT nodeList = tag.getList(NBT_CRAFTS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < nodeList.size(); ++i) {
            CraftingTaskNode node = CraftingTaskNode.fromNbt(network, nodeList.getCompound(i));
            nodes.put(node.getPattern(), node);
        }
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

        ListNBT nodeList = new ListNBT();
        for (CraftingTaskNode node : this.nodes.all()) {
            nodeList.add(node.writeToNbt());
        }
        tag.put(NBT_CRAFTS, nodeList);

        return tag;
    }

    @Override
    public ICalculationResult calculate() {
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

        int qtyPerCraft = getQuantityPerCraft(requested.getItem(), requested.getFluid(), pattern);
        int qty = ((quantity - 1) / qtyPerCraft) + 1;

        ICalculationResult result = calculateInternal(qty, storageSource, fluidStorageSource, results, fluidResults, pattern, true);

        if (requested.getItem() != null) {
            craftingPreviewInfo.getToCraft().add(ItemHandlerHelper.copyStackWithSize(requested.getItem(), qty * qtyPerCraft));
        } else if (requested.getFluid() != null) {
            craftingPreviewInfo.getToCraftFluids().add(StackUtils.copy(requested.getFluid(), qty * qtyPerCraft));
        }

        return result;
    }

    private ICalculationResult calculateInternal(
        int qty,
        IStackList<ItemStack> storageSource,
        IStackList<FluidStack> fluidStorageSource,
        IStackList<ItemStack> results,
        IStackList<FluidStack> fluidResults,
        ICraftingPattern pattern,
        boolean root) {

        if (System.currentTimeMillis() - calculationStarted > RS.SERVER_CONFIG.getAutocrafting().getCalculationTimeoutMs()) {
            return new CalculationResult(CalculationResultType.TOO_COMPLEX);
        }

        if (!patternsUsed.add(pattern)) {
            return new CalculationResult(CalculationResultType.RECURSIVE, pattern);
        }

        IStackList<ItemStack> itemsToExtract = API.instance().createItemStackList();
        IStackList<FluidStack> fluidsToExtract = API.instance().createFluidStackList();

        CraftingPatternInputs inputs = new CraftingPatternInputs(pattern);

        CraftingTaskNode node = nodes.createOrAddToExistingNode(pattern, root, inputs.getRecipe(), qty);

        ICalculationResult result = calculateForItems(qty, storageSource, fluidStorageSource, results, fluidResults, itemsToExtract, inputs, node);
        if (!result.isOk()) {
            return result;
        }

        if (node instanceof RecipeCraftingTaskNode) {
            ItemStack output = pattern.getOutput(inputs.getRecipe());
            results.add(output, output.getCount() * qty);

            for (ItemStack byproduct : pattern.getByproducts(inputs.getRecipe())) {
                results.add(byproduct, byproduct.getCount() * qty);
            }
        } else if (node instanceof ProcessingCraftingTaskNode) {
            ProcessingCraftingTaskNode processing = (ProcessingCraftingTaskNode) node;

            result = calculateForFluids(qty, storageSource, fluidStorageSource, results, fluidResults, pattern, fluidsToExtract, processing);
            if (!result.isOk()) {
                return result;
            }

            for (ItemStack output : pattern.getOutputs()) {
                results.add(output, output.getCount() * qty);
            }

            for (FluidStack output : pattern.getFluidOutputs()) {
                fluidResults.add(output, output.getAmount() * qty);
            }

            //only add this once
            if (processing.getItemsToReceive().isEmpty()) {
                pattern.getOutputs().forEach(processing::addItemsToReceive);
            }

            if (processing.getFluidsToReceive().isEmpty()) {
                pattern.getFluidOutputs().forEach(processing::addFluidsToReceive);
            }
        }

        patternsUsed.remove(pattern);

        return new CalculationResult(CalculationResultType.OK);
    }

    private ICalculationResult calculateForItems(int qty, IStackList<ItemStack> storageSource, IStackList<FluidStack> fluidStorageSource, IStackList<ItemStack> results, IStackList<FluidStack> fluidResults, IStackList<ItemStack> itemsToExtract, CraftingPatternInputs inputs, CraftingTaskNode node) {
        int ingredientNumber = -1;

        for (CraftingPatternInputs.Ingredient ingredient : inputs.getIngredients()) {
            ingredientNumber++;

            PossibleInputs possibleInputs = new PossibleInputs(ingredient.getInputs());
            possibleInputs.sort(storageSource, results);

            ItemStack possibleInput = possibleInputs.get();

            ItemStack fromSelf = results.get(possibleInput);
            ItemStack fromNetwork = storageSource.get(possibleInput);

            int remaining = ingredient.getCount() * qty;

            if (remaining < 0) { // int overflow
                return new CalculationResult(CalculationResultType.TOO_COMPLEX);
            }

            while (remaining > 0) {
                if (fromSelf != null) {
                    int toTake = Math.min(remaining, fromSelf.getCount());

                    node.addItemsToUse(ingredientNumber, possibleInput, toTake, ingredient.getCount());

                    results.remove(fromSelf, toTake);

                    remaining -= toTake;

                    fromSelf = results.get(possibleInput);
                }

                if (fromNetwork != null && remaining > 0) {
                    int toTake = Math.min(remaining, fromNetwork.getCount());

                    craftingPreviewInfo.getToTake().add(possibleInput, toTake);

                    node.addItemsToUse(ingredientNumber, possibleInput, toTake, ingredient.getCount());

                    storageSource.remove(fromNetwork, toTake);

                    remaining -= toTake;

                    fromNetwork = storageSource.get(possibleInput);

                    toExtractInitial.add(possibleInput, toTake);
                }

                if (remaining > 0) {
                    ICraftingPattern subPattern = network.getCraftingManager().getPattern(possibleInput);

                    if (subPattern != null) {
                        int qtyPerCraft = getQuantityPerCraft(possibleInput, null, subPattern);
                        int subQty = ((remaining - 1) / qtyPerCraft) + 1;

                        ICalculationResult result = calculateInternal(subQty, storageSource, fluidStorageSource, results, fluidResults, subPattern, false);
                        if (!result.isOk()) {
                            return result;
                        }

                        fromSelf = results.get(possibleInput);
                        if (fromSelf == null) {
                            throw new IllegalStateException("Recursive calculation didn't yield anything");
                        }

                        fromNetwork = storageSource.get(possibleInput);

                        // fromSelf contains the amount crafted after the loop.
                        craftingPreviewInfo.getToCraft().add(fromSelf.copy());
                    } else {
                        if (!possibleInputs.cycle()) {
                            // Give up.
                            possibleInput = possibleInputs.get(); // Revert back to 0.

                            craftingPreviewInfo.getMissing().add(possibleInput, remaining);

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

        return new CalculationResult(CalculationResultType.OK);
    }

    private ICalculationResult calculateForFluids(int qty, IStackList<ItemStack> storageSource, IStackList<FluidStack> fluidStorageSource, IStackList<ItemStack> results, IStackList<FluidStack> fluidResults, ICraftingPattern pattern, IStackList<FluidStack> fluidsToExtract, ProcessingCraftingTaskNode processing) {
        for (NonNullList<FluidStack> fluidInputs : pattern.getFluidInputs()) {
            if (fluidInputs.isEmpty()) {
                continue;
            }

            PossibleFluidInputs possibleInputs = new PossibleFluidInputs(new ArrayList<>(fluidInputs));
            possibleInputs.sort(fluidStorageSource, fluidResults);

            FluidStack possibleInput = possibleInputs.get();

            FluidStack fromSelf = fluidResults.get(possibleInput, IComparer.COMPARE_NBT);
            FluidStack fromNetwork = fluidStorageSource.get(possibleInput, IComparer.COMPARE_NBT);

            int remaining = possibleInput.getAmount() * qty;

            if (remaining < 0) { // int overflow
                return new CalculationResult(CalculationResultType.TOO_COMPLEX);
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

                    craftingPreviewInfo.getToTakeFluids().add(possibleInput, toTake);

                    fluidStorageSource.remove(fromNetwork, toTake);

                    remaining -= toTake;

                    fromNetwork = fluidStorageSource.get(possibleInput, IComparer.COMPARE_NBT);

                    toExtractInitialFluids.add(possibleInput, toTake);
                }

                if (remaining > 0) {
                    ICraftingPattern subPattern = network.getCraftingManager().getPattern(possibleInput);

                    if (subPattern != null) {
                        int qtyPerCraft = getQuantityPerCraft(null, possibleInput, subPattern);
                        int subQty = ((remaining - 1) / qtyPerCraft) + 1;

                        ICalculationResult result = calculateInternal(subQty, storageSource, fluidStorageSource, results, fluidResults, subPattern, false);
                        if (!result.isOk()) {
                            return result;
                        }

                        fromSelf = fluidResults.get(possibleInput, IComparer.COMPARE_NBT);
                        if (fromSelf == null) {
                            throw new IllegalStateException("Recursive fluid calculation didn't yield anything");
                        }

                        fromNetwork = fluidStorageSource.get(possibleInput, IComparer.COMPARE_NBT);

                        // fromSelf contains the amount crafted after the loop.
                        craftingPreviewInfo.getToCraftFluids().add(fromSelf.copy());
                    } else {
                        if (!possibleInputs.cycle()) {
                            // Give up.
                            possibleInput = possibleInputs.get(); // Revert back to 0.

                            craftingPreviewInfo.getMissingFluids().add(possibleInput, remaining);

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

        return new CalculationResult(CalculationResultType.OK);
    }

    @Override
    public void start() {
        if (hasMissing()) {
            throw new RuntimeException("Cannot start crafting task with missing stacks");
        }

        nodes.all().forEach(node -> {
            totalSteps += node.getQuantity();
            node.finishCalculation();
        });

        executionStarted = System.currentTimeMillis();

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
        if (hasMissing()) {
            throw new RuntimeException("Cannot call update on a crafting task that has missing items");
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
            IoUtil.extractItemsFromNetwork(toExtractInitial, network, internalStorage);
            IoUtil.extractFluidsFromNetwork(toExtractInitialFluids, network, internalFluidStorage);

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

    private int getQuantityPerCraft(@Nullable ItemStack item, @Nullable FluidStack fluid, ICraftingPattern pattern) {
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
        } else if (fluid != null) {
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
        for (CraftingTaskNode node : this.nodes.all()) {
            if (node instanceof ProcessingCraftingTaskNode) {
                ProcessingCraftingTaskNode processing = (ProcessingCraftingTaskNode) node;

                int needed = processing.getNeeded(stack);
                if (needed > 0) {
                    if (needed > size) {
                        needed = size;
                    }

                    processing.addFinished(stack, needed);

                    size -= needed;

                    if (!processing.isRoot()) {
                        internalStorage.insert(stack, needed, Action.PERFORM);
                    } else {
                        ItemStack remainder = network.insertItem(stack, needed, Action.PERFORM);

                        internalStorage.insert(remainder, remainder.getCount(), Action.PERFORM);
                    }

                    if (processing.updateFinished()) { //only update if finished changes
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
        for (CraftingTaskNode node : this.nodes.all()) {
            if (node instanceof ProcessingCraftingTaskNode) {
                ProcessingCraftingTaskNode processing = (ProcessingCraftingTaskNode) node;

                int needed = processing.getNeeded(stack);

                if (needed > 0) {
                    if (needed > size) {
                        needed = size;
                    }

                    processing.addFinished(stack, needed);

                    size -= needed;

                    if (!processing.isRoot()) {
                        internalFluidStorage.insert(stack, needed, Action.PERFORM);
                    } else {
                        FluidStack remainder = network.insertFluid(stack, needed, Action.PERFORM);

                        internalFluidStorage.insert(remainder, remainder.getAmount(), Action.PERFORM);
                    }

                    if (processing.updateFinished()) { //only update if finished changes
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
        return craftingMonitorElementFactory.getElements(nodes.all(), internalStorage, internalFluidStorage);
    }

    @Override
    public List<ICraftingPreviewElement<?>> getPreviewElements() {
        return craftingPreviewElementFactory.getElements(craftingPreviewInfo);
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
    public boolean hasMissing() {
        return craftingPreviewInfo.hasMissing();
    }

    @Override
    public UUID getId() {
        return id;
    }
}
