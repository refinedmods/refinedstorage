package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6;

import com.google.common.collect.ImmutableList;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.refinedmods.refinedstorage.api.autocrafting.task.*;
import com.refinedmods.refinedstorage.api.autocrafting.task.interceptor.IOutputInterceptor;
import com.refinedmods.refinedstorage.api.autocrafting.task.interceptor.IOutputInterceptorFactory;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.ErrorCraftingMonitorElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.FluidCraftingMonitorElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.craftingmonitor.ItemCraftingMonitorElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.FluidCraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.preview.ItemCraftingPreviewElement;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.FluidStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.ItemStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.FluidStorageDiskFactory;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.ItemStorageDiskFactory;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
    private static final String NBT_CRAFTS = "Crafts";
    private static final String NBT_MISSING = "Missing";
    private static final String NBT_MISSING_FLUIDS = "MissingFluids";
    private static final String NBT_TOTAL_STEPS = "TotalSteps";
    private static final String NBT_CURRENT_STEP = "CurrentStep";
    private static final String NBT_PATTERN_STACK = "Stack";
    private static final String NBT_PATTERN_CONTAINER_POS = "ContainerPos";
    private static final String NBT_OUTPUT_INTERCEPTORS = "OutputInterceptors";

    private static final int DEFAULT_EXTRACT_FLAGS = IComparer.COMPARE_NBT;

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
    private int currentstep;
    private final Set<ICraftingPattern> patternsUsed = new HashSet<>();

    private final IStorageDisk<ItemStack> internalStorage;
    private final IStorageDisk<FluidStack> internalFluidStorage;

    private IStackList<ItemStack> toExtractInitial = API.instance().createItemStackList();
    private IStackList<FluidStack> toExtractInitialFluids = API.instance().createFluidStackList();

    private final Map<ICraftingPattern, Craft> crafts = new LinkedHashMap<>();
    private final List<Craft> toRemove = new ArrayList<>();

    private IStackList<ItemStack> missing = API.instance().createItemStackList();
    private IStackList<FluidStack> missingFluids = API.instance().createFluidStackList();

    private final IStackList<ItemStack> toTake = API.instance().createItemStackList();
    private final IStackList<FluidStack> toTakeFluids = API.instance().createFluidStackList();

    private final List<ItemStack> toCraft = new ArrayList<>();
    private final List<FluidStack> toCraftFluids = new ArrayList<>();

    private final Set<IOutputInterceptor> outputInterceptors = new HashSet<>();

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
        this.pattern = readPatternFromNbt(tag.getCompound(NBT_PATTERN), network.getWorld());
        this.ticks = tag.getInt(NBT_TICKS);
        this.id = tag.getUniqueId(NBT_ID);
        this.executionStarted = tag.getLong(NBT_EXECUTION_STARTED);
        this.totalSteps = tag.getInt(NBT_TOTAL_STEPS);
        this.currentstep = tag.getInt(NBT_CURRENT_STEP);

        ItemStorageDiskFactory factoryItem = new ItemStorageDiskFactory();
        FluidStorageDiskFactory factoryFluid = new FluidStorageDiskFactory();

        this.internalStorage = factoryItem.createFromNbt(null, tag.getCompound(NBT_INTERNAL_STORAGE));
        this.internalFluidStorage = factoryFluid.createFromNbt(null, tag.getCompound(NBT_INTERNAL_FLUID_STORAGE));

        this.toExtractInitial = readItemStackList(tag.getList(NBT_TO_EXTRACT_INITIAL, Constants.NBT.TAG_COMPOUND));
        this.toExtractInitialFluids = readFluidStackList(tag.getList(NBT_TO_EXTRACT_INITIAL_FLUIDS, Constants.NBT.TAG_COMPOUND));

        ListNBT craftList = tag.getList(NBT_CRAFTS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < craftList.size(); ++i) {
            Craft c = Craft.createCraftFromNBT(network, craftList.getCompound(i));
            crafts.put(c.getPattern(), c);
        }

        if (tag.contains(NBT_OUTPUT_INTERCEPTORS)) {
            ListNBT outputInterceptorList = tag.getList(NBT_OUTPUT_INTERCEPTORS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < outputInterceptorList.size(); ++i) {
                CompoundNBT interceptorTag = outputInterceptorList.getCompound(i);

                ResourceLocation type = new ResourceLocation(interceptorTag.getString("Type"));

                IOutputInterceptorFactory factory = API.instance().getOutputInterceptorRegistry().get(type);
                if (factory == null) {
                    throw new CraftingTaskReadException("Output interceptor with type " + type + " not found");
                }

                outputInterceptors.add(factory.create(interceptorTag));
            }
        }

        this.missing = readItemStackList(tag.getList(NBT_MISSING, Constants.NBT.TAG_COMPOUND));
        this.missingFluids = readFluidStackList(tag.getList(NBT_MISSING_FLUIDS, Constants.NBT.TAG_COMPOUND));
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.put(NBT_REQUESTED, requested.writeToNbt());
        tag.putInt(NBT_QUANTITY, quantity);
        tag.put(NBT_PATTERN, writePatternToNbt(pattern));
        tag.putInt(NBT_TICKS, ticks);
        tag.putUniqueId(NBT_ID, id);
        tag.putLong(NBT_EXECUTION_STARTED, executionStarted);
        tag.put(NBT_INTERNAL_STORAGE, internalStorage.writeToNbt());
        tag.put(NBT_INTERNAL_FLUID_STORAGE, internalFluidStorage.writeToNbt());
        tag.put(NBT_TO_EXTRACT_INITIAL, writeItemStackList(toExtractInitial));
        tag.put(NBT_TO_EXTRACT_INITIAL_FLUIDS, writeFluidStackList(toExtractInitialFluids));
        tag.putInt(NBT_TOTAL_STEPS, totalSteps);
        tag.putInt(NBT_CURRENT_STEP, currentstep);

        ListNBT craftingList = new ListNBT();
        for (Craft craft : this.crafts.values()) {
            craftingList.add(craft.writeToNbt());
        }
        tag.put(NBT_CRAFTS, craftingList);

        ListNBT outputInterceptorList = new ListNBT();
        for (IOutputInterceptor interceptor : this.outputInterceptors) {
            CompoundNBT interceptorTag = new CompoundNBT();
            interceptorTag.putString("Type", interceptor.getId().toString());
            interceptorTag = interceptor.writeToNbt(interceptorTag);

            outputInterceptorList.add(interceptorTag);
        }

        tag.put(NBT_OUTPUT_INTERCEPTORS, outputInterceptorList);

        tag.put(NBT_MISSING, writeItemStackList(missing));
        tag.put(NBT_MISSING_FLUIDS, writeFluidStackList(missingFluids));

        return tag;
    }

    static ListNBT writeItemStackList(IStackList<ItemStack> stacks) {
        ListNBT list = new ListNBT();

        for (StackListEntry<ItemStack> entry : stacks.getStacks()) {
            list.add(StackUtils.serializeStackToNbt(entry.getStack()));
        }

        return list;
    }

    static IStackList<ItemStack> readItemStackList(ListNBT list) throws CraftingTaskReadException {
        IStackList<ItemStack> stacks = API.instance().createItemStackList();

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(list.getCompound(i));

            if (stack.isEmpty()) {
                throw new CraftingTaskReadException("Empty stack!");
            }

            stacks.add(stack);
        }

        return stacks;
    }

    static ListNBT writeFluidStackList(IStackList<FluidStack> stacks) {
        ListNBT list = new ListNBT();

        for (StackListEntry<FluidStack> entry : stacks.getStacks()) {
            list.add(entry.getStack().writeToNBT(new CompoundNBT()));
        }

        return list;
    }

    static IStackList<FluidStack> readFluidStackList(ListNBT list) throws CraftingTaskReadException {
        IStackList<FluidStack> stacks = API.instance().createFluidStackList();

        for (int i = 0; i < list.size(); ++i) {
            FluidStack stack = FluidStack.loadFluidStackFromNBT(list.getCompound(i));

            if (stack.isEmpty()) {
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

        IStackList<ItemStack> results = API.instance().createItemStackList();
        IStackList<FluidStack> fluidResults = API.instance().createFluidStackList();

        IStackList<ItemStack> storage = network.getItemStorageCache().getList().copy();
        IStackList<FluidStack> fluidStorage = network.getFluidStorageCache().getList().copy();

        int qtyPerCraft = getQuantityPerCraft(requested.getItem(), requested.getFluid(), this.pattern);
        int qty = ((this.quantity - 1) / qtyPerCraft) + 1; //CeilDiv

        ICraftingTaskError result = calculateInternal(qty, storage, fluidStorage, results, fluidResults, this.pattern, true);

        if (result != null) {
            return result;
        }

        if (requested.getItem() != null) {
            ItemStack req = requested.getItem().copy();
            req.setCount(qty);
            this.toCraft.add(req);
        } else {
            FluidStack req = requested.getFluid().copy();
            req.setAmount(qty);
            this.toCraftFluids.add(req);
        }

        if (missing.isEmpty()) {
            crafts.values().forEach(c -> {
                totalSteps += c.getQuantity();

                if (c instanceof Processing) {
                    ((Processing) c).finishCalculation();
                }
            });
        }

        return null;
    }

    static class PossibleInputs {
        private final List<ItemStack> possibilities;
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

    static class PossibleFluidInputs {
        private final List<FluidStack> possibilities;
        private int pos;

        PossibleFluidInputs(List<FluidStack> possibilities) {
            this.possibilities = possibilities;
        }

        FluidStack get() {
            return possibilities.get(pos);
        }

        // Return false if we're exhausted.
        // TODO: never called?
        boolean cycle() {
            if (pos + 1 >= possibilities.size()) {
                pos = 0;

                return false;
            }

            pos++;

            return true;
        }

        void sort(IStackList<FluidStack> mutatedStorage, IStackList<FluidStack> results) {
            possibilities.sort((a, b) -> {
                FluidStack ar = mutatedStorage.get(a);
                FluidStack br = mutatedStorage.get(b);

                return (br == null ? 0 : br.getAmount()) - (ar == null ? 0 : ar.getAmount());
            });

            possibilities.sort((a, b) -> {
                FluidStack ar = results.get(a);
                FluidStack br = results.get(b);

                return (br == null ? 0 : br.getAmount()) - (ar == null ? 0 : ar.getAmount());
            });
        }
    }

    @Nullable
    private ICraftingTaskError calculateInternal(
        int qty,
        IStackList<ItemStack> mutatedStorage,
        IStackList<FluidStack> mutatedFluidStorage,
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
        Craft craft = crafts.get(pattern);
        if (craft == null) {
            craft = pattern.isProcessing() ? new Processing(pattern, root) : new Crafting(pattern, root, recipe);
            crafts.put(pattern, craft);
        }
        craft.addQuantity(qty);

        int ingredientNumber = -1;

        for (Pair<NonNullList<ItemStack>, Integer> pair : ingredients) {
            ingredientNumber++;

            PossibleInputs possibleInputs = new PossibleInputs(new ArrayList<>(pair.getLeft()));
            possibleInputs.sort(mutatedStorage, results);

            ItemStack possibleInput = possibleInputs.get();

            ItemStack fromSelf = results.get(possibleInput);
            ItemStack fromNetwork = mutatedStorage.get(possibleInput);

            int remaining = pair.getRight() * qty;

            if (remaining < 0) { //int overflow
                return new CraftingTaskError(CraftingTaskErrorType.TOO_COMPLEX);
            }

            while (remaining > 0) {
                if (fromSelf != null) {
                    int toTake = Math.min(remaining, fromSelf.getCount());

                    craft.addItemsToUse(ingredientNumber, possibleInput, toTake, pair.getRight());

                    results.remove(fromSelf, toTake);

                    remaining -= toTake;

                    fromSelf = results.get(possibleInput);
                }
                if (fromNetwork != null && remaining > 0) {
                    int toTake = Math.min(remaining, fromNetwork.getCount());

                    this.toTake.add(possibleInput, toTake);

                    craft.addItemsToUse(ingredientNumber, possibleInput, toTake, pair.getRight());

                    mutatedStorage.remove(fromNetwork, toTake);

                    remaining -= toTake;

                    fromNetwork = mutatedStorage.get(possibleInput);

                    toExtractInitial.add(possibleInput, toTake);
                }
                if (remaining > 0) {
                    ICraftingPattern subPattern = network.getCraftingManager().getPattern(possibleInput);

                    if (subPattern != null) {
                        int qtyPerCraft = getQuantityPerCraft(possibleInput, null, subPattern);
                        int subQty = ((remaining - 1) / qtyPerCraft) + 1; //CeilDiv

                        ICraftingTaskError result = calculateInternal(subQty, mutatedStorage, mutatedFluidStorage, results, fluidResults, subPattern, false);

                        if (result != null) {
                            return result;
                        }

                        fromSelf = results.get(possibleInput);
                        if (fromSelf == null) {
                            throw new IllegalStateException("Recursive calculation didn't yield anything");
                        }

                        fromNetwork = mutatedStorage.get(possibleInput);
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
                            fromNetwork = mutatedStorage.get(possibleInput);
                        }
                    }
                }
            }
        }

        if (craft instanceof Crafting) {

            ItemStack output = pattern.getOutput(recipe);
            results.add(output, output.getCount() * qty);

            for (ItemStack byproduct : pattern.getByproducts(recipe)) {
                results.add(byproduct, byproduct.getCount() * qty);
            }

        } else {
            Processing processing = (Processing) craft;

            ingredientNumber = -1;

            for (NonNullList<FluidStack> inputs : pattern.getFluidInputs()) {
                if (inputs.isEmpty()) {
                    continue;
                }
                ingredientNumber++;

                PossibleFluidInputs possibleInputs = new PossibleFluidInputs(new ArrayList<>(inputs));
                possibleInputs.sort(mutatedFluidStorage, fluidResults);

                FluidStack possibleInput = possibleInputs.get();

                FluidStack fromSelf = fluidResults.get(possibleInput, IComparer.COMPARE_NBT);
                FluidStack fromNetwork = mutatedFluidStorage.get(possibleInput, IComparer.COMPARE_NBT);

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

                        mutatedFluidStorage.remove(fromNetwork, toTake);

                        remaining -= toTake;

                        fromNetwork = mutatedFluidStorage.get(possibleInput, IComparer.COMPARE_NBT);

                        toExtractInitialFluids.add(possibleInput, toTake);
                    }
                    if (remaining > 0) {
                        ICraftingPattern subPattern = network.getCraftingManager().getPattern(possibleInput);

                        if (subPattern != null) {
                            int qtyPerCraft = getQuantityPerCraft(null, possibleInput, subPattern);
                            int subQty = ((remaining - 1) / qtyPerCraft) + 1; //CeilDiv

                            ICraftingTaskError result = calculateInternal(subQty, mutatedStorage, mutatedFluidStorage, results, fluidResults, subPattern, false);

                            if (result != null) {
                                return result;
                            }

                            fromSelf = fluidResults.get(possibleInput, IComparer.COMPARE_NBT);
                            if (fromSelf == null) {
                                throw new IllegalStateException("Recursive fluid calculation didn't yield anything");
                            }

                            fromNetwork = mutatedFluidStorage.get(possibleInput, IComparer.COMPARE_NBT);

                            // fromSelf contains the amount crafted after the loop.
                            this.toCraftFluids.add(fromSelf.copy());
                        } else {
                            this.missingFluids.add(possibleInput, remaining);

                            fluidsToExtract.add(possibleInput, remaining);

                            remaining = 0;
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

    private void updateCrafting(Crafting c) {

        for (ICraftingPatternContainer container : network.getCraftingManager().getAllContainer(c.getPattern())) {

            int interval = container.getUpdateInterval();

            if (interval < 0) {
                throw new IllegalStateException(container + " has an update interval of < 0");
            }

            if (interval == 0 || ticks % interval == 0) {
                for (int i = 0; i < container.getMaximumSuccessfulCraftingUpdates(); i++) {

                    if (c.getQuantity() <= 0) {
                        toRemove.add(c);
                        return;
                    }

                    if (extractFromInternalItemStorage(c.getItemsToUse(true).getStacks(), this.internalStorage, Action.SIMULATE) != null) {

                        extractFromInternalItemStorage(c.getItemsToUse(false).getStacks(), this.internalStorage, Action.PERFORM);

                        ItemStack output = c.getPattern().getOutput(c.getRecipe());

                        if (!c.isRoot()) {
                            this.internalStorage.insert(output, output.getCount(), Action.PERFORM);
                        } else {
                            insertOutput(output, output.getCount());
                        }

                        // Byproducts need to always be inserted in the internal storage for later reuse further in the task.
                        // Regular outputs can be inserted into the network *IF* it's a root since it's *NOT* expected to be used later on.
                        for (ItemStack byp : c.getPattern().getByproducts(c.getRecipe())) {
                            this.internalStorage.insert(byp, byp.getCount(), Action.PERFORM);
                        }

                        c.next();
                        currentstep++;
                        network.getCraftingManager().onTaskChanged();
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private void insertOutput(ItemStack output, int count) {
        output = ItemHandlerHelper.copyStackWithSize(output, count);

        for (IOutputInterceptor interceptor : this.outputInterceptors) {
            output = interceptor.intercept(network.getWorld().getServer(), output);
            if (output.isEmpty()) {
                return;
            }
        }

        ItemStack remainder = this.network.insertItem(output, output.getCount(), Action.PERFORM);

        this.internalStorage.insert(remainder, remainder.getCount(), Action.PERFORM);
    }

    private void insertOutput(FluidStack output, int count) {
        output = StackUtils.copy(output, count);

        for (IOutputInterceptor interceptor : this.outputInterceptors) {
            output = interceptor.intercept(network.getWorld().getServer(), output);
            if (output.isEmpty()) {
                return;
            }
        }

        FluidStack remainder = network.insertFluid(output, count, Action.PERFORM);

        this.internalFluidStorage.insert(remainder, remainder.getAmount(), Action.PERFORM);
    }

    private void updateProcessing(Processing p) {

        if (p.getState() == ProcessingState.PROCESSED) {
            toRemove.add(p);
            network.getCraftingManager().onTaskChanged();
            return;
        }
        //These are for handling multiple crafters with differing states
        boolean allLocked = true;
        boolean allNull = true;
        boolean allRejected = true;

        ProcessingState originalState = p.getState();

        for (ICraftingPatternContainer container : network.getCraftingManager().getAllContainer(p.getPattern())) {
            int interval = container.getUpdateInterval();

            if (interval < 0) {
                throw new IllegalStateException(container + " has an update interval of < 0");
            }

            if (interval == 0 || ticks % interval == 0) {

                for (int i = 0; i < container.getMaximumSuccessfulCraftingUpdates(); i++) {
                    if (p.getQuantity() <= 0) {
                        return;
                    }

                    if (container.isLocked()) {
                        if (allLocked) {
                            p.setState(ProcessingState.LOCKED);
                        }
                        break;
                    } else {
                        allLocked = false;
                    }
                    if (p.hasItems() && container.getConnectedInventory() == null
                        || p.hasFluids() && container.getConnectedFluidInventory() == null) {
                        if (allNull) {
                            p.setState(ProcessingState.MACHINE_NONE);
                        }
                        break;
                    } else {
                        allNull = false;
                    }

                    boolean hasAll = false;
                    IStackList<ItemStack> extractedItems;
                    IStackList<FluidStack> extractedFluids = null;

                    extractedItems = extractFromInternalItemStorage(p.getItemsToUse(true).getStacks(), this.internalStorage, Action.SIMULATE);
                    if (extractedItems != null) {
                        extractedFluids = extractFromInternalFluidStorage(p.getFluidsToUse().getStacks(), this.internalFluidStorage, Action.SIMULATE);
                        if (extractedFluids != null) {
                            hasAll = true;
                        }
                    }

                    boolean canInsert = false;
                    if (hasAll) {
                        canInsert = insertIntoInventory(container.getConnectedInventory(), extractedItems.getStacks(), Action.SIMULATE);
                        if (canInsert) {
                            canInsert = insertIntoTank(container.getConnectedFluidInventory(), extractedFluids.getStacks(), Action.SIMULATE);
                        }
                    }

                    if (hasAll && !canInsert) {
                        if (allRejected) {
                            p.setState(ProcessingState.MACHINE_DOES_NOT_ACCEPT);
                        }
                        break;
                    } else {
                        allRejected = false;
                    }

                    if (hasAll && canInsert) {
                        p.setState(ProcessingState.READY);

                        extractFromInternalItemStorage(p.getItemsToUse(false).getStacks(), this.internalStorage, Action.PERFORM);
                        extractFromInternalFluidStorage(p.getFluidsToUse().getStacks(), this.internalFluidStorage, Action.PERFORM);

                        insertIntoInventory(container.getConnectedInventory(), extractedItems.getStacks(), Action.PERFORM);
                        insertIntoTank(container.getConnectedFluidInventory(), extractedFluids.getStacks(), Action.PERFORM);

                        p.next();
                        currentstep++;
                        network.getCraftingManager().onTaskChanged();
                        container.onUsedForProcessing();

                    }
                }

            }
        }
        if (originalState != p.getState()) {
            network.getCraftingManager().onTaskChanged();
        }
    }

    private static IStackList<ItemStack> extractFromInternalItemStorage(Collection<StackListEntry<ItemStack>> stacks, IStorageDisk<ItemStack> storage, Action action) {
        IStackList<ItemStack> toReturn = API.instance().createItemStackList();
        for (StackListEntry<ItemStack> entry : stacks) {
            ItemStack result = storage.extract(entry.getStack(), entry.getStack().getCount(), DEFAULT_EXTRACT_FLAGS, action);

            if (result == ItemStack.EMPTY || result.getCount() != entry.getStack().getCount()) {
                if (action == Action.PERFORM) {
                    throw new IllegalStateException("The internal crafting inventory reported that " + entry.getStack() + " was available but we got " + result);
                }
                return null;
            }
            toReturn.add(result);
        }
        return toReturn;
    }

    private static IStackList<FluidStack> extractFromInternalFluidStorage(Collection<StackListEntry<FluidStack>> stacks, IStorageDisk<FluidStack> storage, Action action) {
        IStackList<FluidStack> toReturn = API.instance().createFluidStackList();
        for (StackListEntry<FluidStack> entry : stacks) {
            FluidStack result = storage.extract(entry.getStack(), entry.getStack().getAmount(), IComparer.COMPARE_NBT, action);
            if (result == FluidStack.EMPTY || result.getAmount() != entry.getStack().getAmount()) {
                if (action == Action.PERFORM) {
                    throw new IllegalStateException("The internal crafting inventory reported that " + entry.getStack() + " was available but we got " + result);
                }
                return null;
            }
            toReturn.add(result);
        }
        return toReturn;
    }


    private static boolean insertIntoInventory(@Nullable IItemHandler dest, Collection<StackListEntry<ItemStack>> toInsert, Action action) {
        if (dest == null) {
            return false;
        }
        if (toInsert.isEmpty()) {
            return true;
        }
        Deque<StackListEntry<ItemStack>> stacks = new ArrayDeque<>(toInsert);

        StackListEntry<ItemStack> currentEntry = stacks.poll();

        ItemStack current = currentEntry != null ? currentEntry.getStack() : null;

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
                currentEntry = stacks.poll();

                current = currentEntry != null ? currentEntry.getStack() : null;
            } else if (current.getCount() == remainder.getCount()) { // If we didn't insert anything over ALL these slots, stop here.
                break;
            } else { // If we didn't insert all, continue with other slots and use our remainder.
                current = remainder;
            }
        }

        boolean success = current == null && stacks.isEmpty();
        if (!success && action == Action.PERFORM) {
            LOGGER.warn("Item Handler unexpectedly didn't accept " + (current != null ? current.getTranslationKey() : null) + ", the remainder has been voided!");
        }
        return success;
    }

    private static boolean insertIntoTank(IFluidHandler dest, Collection<StackListEntry<FluidStack>> toInsert, Action action) {
        for (StackListEntry<FluidStack> entry : toInsert) {
            int filled = dest.fill(entry.getStack(), action == Action.SIMULATE ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
            if (filled != entry.getStack().getAmount()) {
                if (action == Action.PERFORM) {
                    LOGGER.warn("Fluid Handler unexpectedly didn't accept all of " + entry.getStack().getTranslationKey() + ", the remainder has been voided!");
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public int getCompletionPercentage() {
        if (totalSteps == 0) {
            return 0;
        }
        return (int) ((float) currentstep * 100 / totalSteps);
    }

    @Override
    public boolean update() {
        if (hasMissing()) {
            LOGGER.warn("Crafting task with missing items or fluids cannot execute, cancelling...");

            return true;
        }

        if (executionStarted == -1) {
            executionStarted = System.currentTimeMillis();
        }

        ++ticks;
        if (ticks < 20 * 10) {
            return false;
        }

        if (this.crafts.isEmpty()) {
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

            for (Craft craft : crafts.values()) {
                if (craft instanceof Crafting) {
                    updateCrafting((Crafting) craft);
                } else {
                    updateProcessing((Processing) craft);
                }
            }

            for (Craft craft : toRemove) {
                crafts.remove(craft.getPattern());
            }
            toRemove.clear();

            return false;
        }
    }

    @Override
    public void onCancelled() {
        crafts.values().forEach(c -> {
            if (c instanceof Processing) {
                network.getCraftingManager().getAllContainer(c.getPattern()).forEach(ICraftingPatternContainer::unlock);
            }
        });

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
        for (Craft craft : this.crafts.values()) {
            if (craft instanceof Processing) {
                Processing p = (Processing) craft;

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
                        insertOutput(stack, needed);
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
        for (Craft craft : this.crafts.values()) {
            if (craft instanceof Processing) {
                Processing p = (Processing) craft;

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
                        insertOutput(stack, needed);
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

    static CompoundNBT writePatternToNbt(ICraftingPattern pattern) {
        CompoundNBT tag = new CompoundNBT();

        tag.put(NBT_PATTERN_STACK, pattern.getStack().serializeNBT());
        tag.putLong(NBT_PATTERN_CONTAINER_POS, pattern.getContainer().getPosition().toLong());

        return tag;
    }

    static ICraftingPattern readPatternFromNbt(CompoundNBT tag, World world) throws CraftingTaskReadException {
        BlockPos containerPos = BlockPos.fromLong(tag.getLong(NBT_PATTERN_CONTAINER_POS));

        INetworkNode node = API.instance().getNetworkNodeManager((ServerWorld) world).getNode(containerPos);

        if (node instanceof ICraftingPatternContainer) {
            ItemStack stack = ItemStack.read(tag.getCompound(NBT_PATTERN_STACK));

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

        for (Craft craft : this.crafts.values()) {
            if (craft instanceof Crafting) {
                if (craft.getQuantity() > 0) {
                    Crafting c = (Crafting) craft;
                    for (ItemStack receive : c.getPattern().getOutputs()) {
                        elements.add(new ItemCraftingMonitorElement(receive, 0, 0, 0, 0, receive.getCount() * c.getQuantity()), false);
                    }
                }
            } else {
                Processing p = (Processing) craft;
                if (p.getState() == ProcessingState.PROCESSED) {
                    continue;
                }

                for (StackListEntry<ItemStack> put : p.getItemsToDisplay().getStacks()) {
                    if (p.getProcessing() > 0 || p.getState() != ProcessingState.READY) {
                        ICraftingMonitorElement element = new ItemCraftingMonitorElement(put.getStack(), 0, 0, put.getStack().getCount() * p.getProcessing(), 0, 0);

                        if (p.getState() == ProcessingState.MACHINE_DOES_NOT_ACCEPT) {
                            element = new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.machine_does_not_accept_item");
                        } else if (p.getState() == ProcessingState.MACHINE_NONE) {
                            element = new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.machine_none");
                        } else if (p.getState() == ProcessingState.LOCKED) {
                            element = new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.crafter_is_locked");
                        }
                        elements.add(element, true);
                    }
                }
                for (StackListEntry<ItemStack> receive : p.getItemsToReceive().getStacks()) {
                    int count = p.getNeeded(receive.getStack());
                    if (count > 0) {
                        elements.add(new ItemCraftingMonitorElement(receive.getStack(), 0, 0, 0, count, 0), true);
                    }
                }
                for (StackListEntry<FluidStack> put : p.getFluidsToUse().getStacks()) {
                    if (p.getProcessing() > 0 || p.getState() != ProcessingState.READY) {
                        ICraftingMonitorElement element = new FluidCraftingMonitorElement(put.getStack(), 0, 0, put.getStack().getAmount() * p.getProcessing(), 0, 0);
                        if (p.getState() == ProcessingState.MACHINE_DOES_NOT_ACCEPT) {
                            element = new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.machine_does_not_accept_fluid");
                        } else if (p.getState() == ProcessingState.MACHINE_NONE) {
                            element = new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.machine_none");
                        } else if (p.getState() == ProcessingState.LOCKED) {
                            element = new ErrorCraftingMonitorElement(element, "gui.refinedstorage.crafting_monitor.crafter_is_locked");
                        }
                        elements.add(element, true);
                    }
                }

                for (StackListEntry<FluidStack> receive : p.getFluidsToReceive().getStacks()) {
                    int count = p.getNeeded(receive.getStack());
                    if (count > 0) {
                        elements.add(new FluidCraftingMonitorElement(receive.getStack(), 0, 0, 0, count, 0), true);
                    }
                }
            }
        }

        for (ItemStack stack : this.internalStorage.getStacks()) {
            elements.addStorage(new ItemCraftingMonitorElement(stack, stack.getCount(), 0, 0, 0, 0));
        }

        for (FluidStack stack : this.internalFluidStorage.getStacks()) {
            elements.addStorage(new FluidCraftingMonitorElement(stack, stack.getAmount(), 0, 0, 0, 0));
        }

        elements.commit();

        return elements.getElements();
    }

    @Override
    public List<ICraftingPreviewElement<?>> getPreviewStacks() {
        Map<Integer, ItemCraftingPreviewElement> map = new LinkedHashMap<>();
        Map<Integer, FluidCraftingPreviewElement> mapFluids = new LinkedHashMap<>();

        for (StackListEntry<ItemStack> stack : missing.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack.getStack());

            ItemCraftingPreviewElement previewStack = map.get(hash);

            if (previewStack == null) {
                previewStack = new ItemCraftingPreviewElement(stack.getStack());
            }

            previewStack.setMissing(true);
            previewStack.addToCraft(stack.getStack().getCount());

            map.put(hash, previewStack);
        }

        for (StackListEntry<FluidStack> stack : missingFluids.getStacks()) {
            int hash = API.instance().getFluidStackHashCode(stack.getStack());

            FluidCraftingPreviewElement previewStack = mapFluids.get(hash);

            if (previewStack == null) {
                previewStack = new FluidCraftingPreviewElement(stack.getStack());
            }

            previewStack.setMissing(true);
            previewStack.addToCraft(stack.getStack().getAmount());

            mapFluids.put(hash, previewStack);
        }

        for (ItemStack stack : ImmutableList.copyOf(toCraft).reverse()) {
            int hash = API.instance().getItemStackHashCode(stack);

            ItemCraftingPreviewElement previewStack = map.get(hash);

            if (previewStack == null) {
                previewStack = new ItemCraftingPreviewElement(stack.getStack());
            }

            previewStack.addToCraft(stack.getCount());

            map.put(hash, previewStack);
        }

        for (FluidStack stack : ImmutableList.copyOf(toCraftFluids).reverse()) {
            int hash = API.instance().getFluidStackHashCode(stack);

            FluidCraftingPreviewElement previewStack = mapFluids.get(hash);

            if (previewStack == null) {
                previewStack = new FluidCraftingPreviewElement(stack);
            }

            previewStack.addToCraft(stack.getAmount());

            mapFluids.put(hash, previewStack);
        }

        for (StackListEntry<ItemStack> stack : toTake.getStacks()) {
            int hash = API.instance().getItemStackHashCode(stack.getStack());

            ItemCraftingPreviewElement previewStack = map.get(hash);

            if (previewStack == null) {
                previewStack = new ItemCraftingPreviewElement(stack.getStack());
            }

            previewStack.addAvailable(stack.getStack().getCount());

            map.put(hash, previewStack);
        }

        for (StackListEntry<FluidStack> stack : toTakeFluids.getStacks()) {
            int hash = API.instance().getFluidStackHashCode(stack.getStack());

            FluidCraftingPreviewElement previewStack = mapFluids.get(hash);

            if (previewStack == null) {
                previewStack = new FluidCraftingPreviewElement(stack.getStack());
            }

            previewStack.addAvailable(stack.getStack().getAmount());

            mapFluids.put(hash, previewStack);
        }

        List<ICraftingPreviewElement<?>> elements = new ArrayList<>();

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

    @Override
    public void addOutputInterceptor(IOutputInterceptor interceptor) {
        outputInterceptors.add(interceptor);
    }
}
