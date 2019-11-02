package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.google.common.collect.HashMultimap;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.*;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElementList;
import com.raoulvdberge.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.*;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
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
import net.minecraft.nbt.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
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
    private static final String NBT_ITEMS_TO_EXPECT = "ItemsToExpect";
    private static final String NBT_FLUIDS_TO_EXPECT = "FluidsToExpect";
    private static final String NBT_ITEM_HASHCODE = "ItemHashcode";
    private static final String NBT_PATTERN_HASHCODE = "PatternHashcode";
    private static final String NBT_FLUID_HASHCODE = "FluidHashcode";
    private static final String NBT_MISSING = "Missing";
    private static final String NBT_MISSING_FLUIDS = "MissingFluids";
    private static final String NBT_TOTAL_STEPS = "TotalSteps";
    private static final String NBT_CURRENT_STEP = "CurrentStep";
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
    private int totalSteps = 0;
    private int curentstep = 0;
    private Set<ICraftingPattern> patternsUsed = new HashSet<>();

    private IStorageDisk<ItemStack> internalStorage;
    private IStorageDisk<FluidStack> internalFluidStorage;

    private IStackList<ItemStack> toExtractInitial = API.instance().createItemStackList();
    private IStackList<FluidStack> toExtractInitialFluids = API.instance().createFluidStackList();

    private Map<Integer, Craft> crafts = new LinkedHashMap<>();

    private List<Craft> craftsToRemove = new ArrayList<>();
    private HashMultimap<Integer, Processing> itemsToExpect = HashMultimap.create();
    private HashMultimap<Integer, Processing> fluidsToExpect = HashMultimap.create();

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
        this.totalSteps = tag.getInteger(NBT_TOTAL_STEPS);
        this.curentstep = tag.getInteger(NBT_CURRENT_STEP);

        StorageDiskFactoryItem factoryItem = new StorageDiskFactoryItem();
        StorageDiskFactoryFluid factoryFluid = new StorageDiskFactoryFluid();

        this.internalStorage = factoryItem.createFromNbt(network.world(), tag.getCompoundTag(NBT_INTERNAL_STORAGE));
        this.internalFluidStorage = factoryFluid.createFromNbt(network.world(), tag.getCompoundTag(NBT_INTERNAL_FLUID_STORAGE));

        this.toExtractInitial = readItemStackList(tag.getTagList(NBT_TO_EXTRACT_INITIAL, Constants.NBT.TAG_COMPOUND));
        this.toExtractInitialFluids = readFluidStackList(tag.getTagList(NBT_TO_EXTRACT_INITIAL_FLUIDS, Constants.NBT.TAG_COMPOUND));

        NBTTagList craftingList = tag.getTagList(NBT_CRAFTS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < craftingList.tagCount(); ++i) {
            Craft c = Craft.createCraftFromNBT(network, craftingList.getCompoundTagAt(i));
            crafts.put(c.getPattern().getChainHashCode(), c);
        }

        NBTTagList expectedItemList = tag.getTagList(NBT_ITEMS_TO_EXPECT, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < expectedItemList.tagCount(); ++i) {
            NBTTagCompound compound = expectedItemList.getCompoundTagAt(i);
            itemsToExpect.put(compound.getInteger(NBT_ITEM_HASHCODE), (Processing) crafts.get(compound.getInteger(NBT_PATTERN_HASHCODE)));
        }

        NBTTagList expectedFluidList = tag.getTagList(NBT_FLUIDS_TO_EXPECT, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < expectedFluidList.tagCount(); ++i) {
            NBTTagCompound compound = expectedFluidList.getCompoundTagAt(i);
            fluidsToExpect.put(compound.getInteger(NBT_FLUID_HASHCODE), (Processing) crafts.get(compound.getInteger(NBT_PATTERN_HASHCODE)));
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
        tag.setInteger(NBT_CURRENT_STEP, curentstep);

        NBTTagList craftingList = new NBTTagList();
        for (Craft craft : this.crafts.values()) {
            craftingList.appendTag(craft.writeToNbt());
        }
        tag.setTag(NBT_CRAFTS, craftingList);

        NBTTagList expectedItemList = new NBTTagList();
        for (Map.Entry<Integer, Processing> entry : itemsToExpect.entries()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger(NBT_ITEM_HASHCODE, entry.getKey());
            compound.setInteger(NBT_PATTERN_HASHCODE, entry.getValue().getPattern().getChainHashCode());
            expectedItemList.appendTag(compound);
        }
        tag.setTag(NBT_ITEMS_TO_EXPECT, expectedItemList);

        NBTTagList expectedFluidList = new NBTTagList();
        for (Map.Entry<Integer, Processing> entry : fluidsToExpect.entries()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger(NBT_FLUID_HASHCODE, entry.getKey());
            compound.setInteger(NBT_PATTERN_HASHCODE, entry.getValue().getPattern().getChainHashCode());
            expectedItemList.appendTag(compound);
        }
        tag.setTag(NBT_FLUIDS_TO_EXPECT, expectedFluidList);


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
            INetworkNodeProxy node = (INetworkNodeProxy) world.getTileEntity(BlockPos.fromLong(list.getCompoundTagAt(i).getLong(NBT_CONTAINER + i)));
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

        IStackList<ItemStack> results = API.instance().createItemStackList();
        IStackList<FluidStack> fluidResults = API.instance().createFluidStackList();

        IStackList<ItemStack> storage = network.getItemStorageCache().getList().copy();
        IStackList<FluidStack> fluidStorage = network.getFluidStorageCache().getList().copy();


        int qtyPerCraft = getQuantityPerCraft(requested.getItem(), requested.getFluid(), pattern);
        int qty = ((this.quantity - 1) / qtyPerCraft) + 1;

        ICraftingTaskError result = calculateInternal(qty, storage, fluidStorage, results, fluidResults, pattern, true);

        if (result != null) {
            return result;
        }
        if (!hasMissing()) {
            for (Craft c : crafts.values()) {
                c.finishCalculation();
                if (c instanceof Processing) {
                    Processing p = (Processing) c;
                    for (ItemStack stack : p.pattern.getOutputs()) {
                        itemsToExpect.put(API.instance().getItemStackHashCode(stack), p);
                    }
                    for (FluidStack stack : p.pattern.getFluidOutputs()) {
                        fluidsToExpect.put(API.instance().getFluidStackHashCode(stack), p);
                    }

                }
            }
        }


        if (requested.getItem() != null) {
            this.toCraft.add(requested.getItem(), qty * qtyPerCraft);
        } else {
            this.toCraftFluids.add(requested.getFluid(), qty * qtyPerCraft);
        }

        return null;
    }


    static class PossibleInputs {
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

        void sort(IStackList<ItemStack> stackList) {
            possibilities.sort((a, b) -> {
                ItemStack ar = stackList.get(a);
                ItemStack br = stackList.get(b);

                return (br == null ? 0 : br.getCount()) - (ar == null ? 0 : ar.getCount());
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

        if (System.currentTimeMillis() - calculationStarted > RS.INSTANCE.config.calculationTimeoutMs) {
            return new CraftingTaskError(CraftingTaskErrorType.TOO_COMPLEX);
        }

        if (!patternsUsed.add(pattern)) {
            return new CraftingTaskError(CraftingTaskErrorType.RECURSIVE, pattern);
        }

        IStackList<ItemStack> itemsToExtract = API.instance().createItemStackList();
        IStackList<FluidStack> fluidsToExtract = API.instance().createFluidStackList();
        boolean duplicate;
        NonNullList<ItemStack> recipe = NonNullList.create();
        Map<NonNullList<ItemStack>, Integer> ingredientList = new LinkedHashMap<>();

        if (pattern.isProcessing()) {
            for (NonNullList<ItemStack> in : pattern.getInputs()) {
                if (!in.isEmpty()) {
                    ingredientList.put(in, in.get(0).getCount());
                }
            }
        } else {
            for (NonNullList<ItemStack> in : pattern.getInputs()) {
                if (in.isEmpty()) {
                    recipe.add(ItemStack.EMPTY);

                } else {
                    recipe.add(in.get(0));
                    duplicate = false;

                    for (Map.Entry<NonNullList<ItemStack>, Integer> entry : ingredientList.entrySet()) {
                        if (API.instance().getComparer().isEqualNoQuantity(in.get(0), entry.getKey().get(0))) {
                            entry.setValue(entry.getValue() + in.get(0).getCount());
                            duplicate = true;
                            break;
                        }
                    }
                    if (!duplicate) {
                        ingredientList.put(in, in.get(0).getCount());
                    }

                }
            }
        }

        Craft craft = createCraft(qty, recipe, pattern, root);
        int IngredientNumber = -1;

        for (Map.Entry<NonNullList<ItemStack>, Integer> entry : ingredientList.entrySet()) {
            IngredientNumber++;
            NonNullList<ItemStack> inputs = entry.getKey();

            PossibleInputs possibleInputs = new PossibleInputs(new ArrayList<>(inputs));
            possibleInputs.sort(mutatedStorage);
            possibleInputs.sort(results);

            ImmutablePair<ItemStack, Integer> possibleInput = new ImmutablePair<>(possibleInputs.get(), entry.getValue() * qty);

            if (possibleInput.getRight() < 0) { // int overflow
                return new CraftingTaskError(CraftingTaskErrorType.TOO_COMPLEX);
            }

            ItemStack fromSelf = results.get(possibleInput.getLeft());
            ItemStack fromNetwork = mutatedStorage.get(possibleInput.getLeft());

            int remaining = possibleInput.getRight();

            while (remaining > 0) {
                if (fromSelf != null) {
                    int toTake = Math.min(remaining, fromSelf.getCount());

                    results.remove(fromSelf, toTake);

                    itemsToExtract.add(possibleInput.getLeft(), toTake);

                    remaining -= toTake;

                    fromSelf = results.get(possibleInput.getLeft());

                    craft.addToItemSets(possibleInput.getLeft(), toTake, entry.getValue(), IngredientNumber);
                }
                if (fromNetwork != null && remaining > 0) {
                    int toTake = Math.min(remaining, fromNetwork.getCount());

                    this.toTake.add(possibleInput.getLeft(), toTake);

                    toExtractInitial.add(possibleInput.getLeft(), toTake);

                    itemsToExtract.add(possibleInput.getLeft(), toTake);

                    mutatedStorage.remove(fromNetwork, toTake);

                    remaining -= toTake;

                    fromNetwork = mutatedStorage.get(possibleInput.getLeft());

                    craft.addToItemSets(possibleInput.getLeft(), toTake, entry.getValue(), IngredientNumber);
                }
                if (remaining > 0) {
                    ICraftingPattern subPattern = network.getCraftingManager().getPattern(possibleInput.getLeft());

                    if (subPattern != null) {
                        int quantityPerCraft = getQuantityPerCraft(possibleInput.getLeft(), null, subPattern);
                        int quantity = ((remaining - 1) / quantityPerCraft) + 1;


                        ICraftingTaskError result = calculateInternal(quantity, mutatedStorage, mutatedFluidStorage, results, fluidResults, subPattern, false);

                        if (result != null) {
                            return result;
                        }

                        fromSelf = results.get(possibleInput.getLeft());
                        if (fromSelf == null) {
                            throw new IllegalStateException("Recursive calculation didn't yield anything");
                        }
                        fromNetwork = mutatedStorage.get(possibleInput.getLeft());

                        // fromSelf contains the amount crafted after the loop.
                        this.toCraft.add(fromSelf);

                    } else {
                        if (!possibleInputs.cycle()) {
                            // Give up.
                            possibleInput = new ImmutablePair<>(possibleInputs.get(), remaining); // Revert back to 0.

                            if (possibleInput.getRight() < 0) { // Int overflow
                                return new CraftingTaskError(CraftingTaskErrorType.TOO_COMPLEX);
                            }
                            this.missing.add(possibleInput.getLeft(), possibleInput.getRight());

                            remaining = 0;

                        } else {
                            // Retry with new input...
                            possibleInput = new ImmutablePair<>(possibleInputs.get(), remaining);

                            fromSelf = results.get(possibleInput.getLeft());
                            fromNetwork = mutatedStorage.get(possibleInput.getLeft());
                        }
                    }
                }
            }
        }

        for (FluidStack in : pattern.getFluidInputs()) {
            FluidStack input = in.copy();
            fluidsToExtract.add(input);
            input.amount = input.amount * qty;
            FluidStack fromSelf = fluidResults.get(input, IComparer.COMPARE_NBT);
            FluidStack fromNetwork = mutatedFluidStorage.get(input, IComparer.COMPARE_NBT);
            int remaining = input.amount;

            while (remaining > 0) {
                if (fromSelf != null) {
                    int toTake = Math.min(remaining, fromSelf.amount);

                    fluidResults.remove(input, toTake);

                    remaining -= toTake;

                    fromSelf = fluidResults.get(input, IComparer.COMPARE_NBT);
                }
                if (fromNetwork != null && remaining > 0) {
                    int toTake = Math.min(remaining, fromNetwork.amount);

                    this.toTakeFluids.add(input, toTake);

                    mutatedFluidStorage.remove(fromNetwork, toTake);

                    remaining -= toTake;

                    fromNetwork = mutatedFluidStorage.get(input, IComparer.COMPARE_NBT);

                    toExtractInitialFluids.add(input, toTake);
                }
                if (remaining > 0) {
                    ICraftingPattern subPattern = network.getCraftingManager().getPattern(input);

                    if (subPattern != null) {
                        int quantityPerCraft = getQuantityPerCraft(null, input, subPattern);
                        int quantity = ((qty - 1) / quantityPerCraft) + 1;

                        ICraftingTaskError result = calculateInternal(quantity, mutatedStorage, mutatedFluidStorage, results, fluidResults, subPattern, false);

                        if (result != null) {
                            return result;
                        }

                        fromSelf = fluidResults.get(input, IComparer.COMPARE_NBT);
                        if (fromSelf == null) {
                            throw new IllegalStateException("Recursive fluid calculation didn't yield anything");
                        }

                        fromNetwork = mutatedFluidStorage.get(input, IComparer.COMPARE_NBT);


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
                results.add(output, output.getCount() * qty);

                itemsToReceive.add(output);
            }

            for (FluidStack output : pattern.getFluidOutputs()) {
                fluidResults.add(output, output.amount * qty);

                fluidsToReceive.add(output);
            }
            Processing processing = (Processing) craft;
            if (!processing.isInitialized) {
                processing.initialize(itemsToReceive, fluidsToReceive, fluidsToExtract);
            }
        } else {
            if (!fluidsToExtract.isEmpty()) {
                throw new IllegalStateException("Cannot extract fluids in normal pattern!");
            }

            ItemStack stack = pattern.getOutput(recipe);
            results.add(stack, stack.getCount() * qty);

            for (ItemStack byproduct : pattern.getByproducts(recipe)) {
                results.add(byproduct, byproduct.getCount() * qty);
            }
        }

        return null;
    }

    private Craft createCraft(int quantity, NonNullList<ItemStack> recipe, ICraftingPattern pattern, boolean root) {
        Craft c = crafts.get(pattern.getChainHashCode());
        if (c != null) {
            boolean found = false;
            c.addQuantity(quantity);
            for (ICraftingPatternContainer container : c.getContainer()) {
                if (API.instance().isNetworkNodeEqual((INetworkNode) container, pattern.getContainer())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                c.addContainer(pattern.getContainer());
            }

        } else {
            c = pattern.isProcessing() ? new Processing(pattern, root) : new Crafting(recipe, pattern, root);
            c.addQuantity(quantity);
            crafts.put(pattern.getChainHashCode(), c);
        }
        return c;
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

    private void updateCrafting(Crafting c) {

        if (c.getQuantity() <= 0) {
            craftsToRemove.add(c);
            return;
        }
        for (ICraftingPatternContainer container : c.getContainer()) {
            int interval = container.getUpdateInterval();

            if (interval < 0) {
                throw new IllegalStateException(container + " has an update interval of < 0");
            }

            if (interval == 0 || ticks % interval == 0) {

                for (int i = 0; i < container.getMaximumSuccessfulCraftingUpdates(); i++) {
                    if (c.getQuantity() <= 0) {
                        return;
                    }

                    if (extractFromInternalItemStorage(c.getNextSet(Action.SIMULATE), internalStorage, Action.SIMULATE)) {

                        extractFromInternalItemStorage(c.getNextSet(Action.PERFORM), internalStorage, Action.PERFORM);


                        //Double checks the recipe, could technically be converted to just and output stack
                        ItemStack output = c.getPattern().getOutput(c.getRecipe());

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
                        for (ItemStack byp : c.getPattern().getByproducts(c.getRecipe())) {
                            this.internalStorage.insert(byp, byp.getCount(), Action.PERFORM);
                        }
                        c.reduceQuantity();
                        curentstep++;
                        network.getCraftingManager().onTaskChanged();

                    } else {
                        break;
                    }
                }
            }
        }

    }

    private void updateProcessing(Processing p) {

        if (p.getState() == ProcessingState.PROCESSED) {
            craftsToRemove.add(p);
            network.getCraftingManager().onTaskChanged();
            return;
        }
        //These are for handling multiple crafters with differing states
        boolean allLocked = true;
        boolean allNull = true;
        boolean allRejected = true;

        ProcessingState originalState = p.getState();

        for (ICraftingPatternContainer container : p.getContainer()) {

            int interval = container.getUpdateInterval();
            if (interval < 0) {
                throw new IllegalStateException(p.getPattern().getContainer() + " has an update interval of < 0");
            }
            if (interval == 0 || ticks % interval == 0) {

                for (int i = 0; i < container.getMaximumSuccessfulCraftingUpdates(); i++) {

                    if (p.getQuantity() == 0) {
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

                    if (container.getConnectedInventory() == null) {
                        if (allNull) {
                            p.setState(ProcessingState.MACHINE_NONE);
                        }
                        break;
                    } else {
                        allNull = false;
                    }

                    boolean hasAll = extractFromInternalItemStorage(p.getNextSet(Action.SIMULATE), internalStorage, Action.SIMULATE);
                    if (hasAll) {
                        hasAll = extractFromInternalFluidStorage(p.getFluidsToPut().getStacks(), internalFluidStorage, Action.SIMULATE);
                    }

                    boolean canInsert = false;
                    if (hasAll) {
                        canInsert = insertIntoInventory(container.getConnectedInventory(), p.getNextSet(Action.SIMULATE), Action.SIMULATE);
                        if (canInsert) {
                            canInsert = insertIntoTank(container.getConnectedFluidInventory(), p.getFluidsToPut().getStacks(), Action.SIMULATE);
                        }
                    }

                    if (canInsert) {
                        allRejected = false;
                    } else {
                        if (hasAll && p.isNothingProcessing() && allRejected) {
                            p.setState(ProcessingState.MACHINE_DOES_NOT_ACCEPT);
                        }
                    }


                    if (hasAll && canInsert) {
                        p.setState(ProcessingState.READY_OR_PROCESSING);
                        extractFromInternalItemStorage(p.getNextSet(Action.SIMULATE), internalStorage, Action.PERFORM); // SIMULATE because the items need to be there for insertion
                        extractFromInternalFluidStorage(p.getFluidsToPut().getStacks(), internalFluidStorage, Action.PERFORM);

                        insertIntoInventory(container.getConnectedInventory(), p.getNextSet(Action.PERFORM), Action.PERFORM);
                        insertIntoTank(container.getConnectedFluidInventory(), p.getFluidsToPut().getStacks(), Action.PERFORM);
                        curentstep++;
                        p.reduceQuantity();

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

    private static boolean extractFromInternalItemStorage(Collection<ItemStack> stacks, IStorageDisk<ItemStack> storage, Action action) {
        for (ItemStack need : stacks) {
            ItemStack result = storage.extract(need, need.getCount(), DEFAULT_EXTRACT_FLAGS, action);

            if (result == null || result.getCount() != need.getCount()) {
                if (action == Action.PERFORM) {
                    throw new IllegalStateException("The internal crafting inventory reported that " + need + " was available but we got " + result);
                }
                return false;
            }
        }
        return true;
    }

    private static boolean extractFromInternalFluidStorage(Collection<FluidStack> stacks, IStorageDisk<FluidStack> storage, Action action) {
        for (FluidStack need : stacks) {
            FluidStack result = storage.extract(need, need.amount, IComparer.COMPARE_NBT, action);
            if (result == null || result.amount != need.amount) {
                if (action == Action.PERFORM) {
                    throw new IllegalStateException("The internal crafting inventory reported that " + need + " was available but we got " + result);
                }
                return false;
            }
        }
        return true;
    }


    private static boolean insertIntoInventory(@Nullable IItemHandler dest, Collection<ItemStack> toInsert, Action action) {
        if (dest == null) {
            return false;
        }
        if (toInsert.isEmpty()) {
            return true;
        }
        Deque<ItemStack> stacks = new ArrayDeque<>(toInsert);

        ItemStack current = stacks.poll();

        List<Integer> availableSlots = IntStream.range(0, dest.getSlots()).boxed().collect(Collectors.toList());

        while (current != null && !availableSlots.isEmpty()) {
            ItemStack remainder = ItemStack.EMPTY;

            for (int i = 0; i < availableSlots.size(); ++i) {
                int slot = availableSlots.get(i);

                // .copy() happens in getNextSet()
                remainder = dest.insertItem(slot, current, action == Action.SIMULATE);

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

        boolean success = current == null && stacks.isEmpty();
        if (!success && action == Action.PERFORM) {
            LOGGER.warn("Item Handler unexpectedly didn't accept " + (current != null ? current.getTranslationKey() : null) + ", the remainder has been voided!");
        }
        return success;
    }

    private static boolean insertIntoTank(IFluidHandler handler, Collection<FluidStack> toInsert, Action action) {
        if (toInsert.isEmpty()) {
            return true;
        }

        int filled;
        for (FluidStack stack : toInsert) {
            filled = handler.fill(stack, action == Action.PERFORM);
            if (filled != stack.amount) {
                if (action == Action.PERFORM) {
                    LOGGER.warn("Fluid Handler unexpectedly didn't accept all of " + stack.getUnlocalizedName() + ", the remainder has been voided!");
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

        return 100 - (int)(((float)curentstep /(float) totalSteps)*100);
    }

    @Override
    public boolean update() {
        if (hasMissing()) {
            LOGGER.warn("Crafting task with missing items or fluids cannot execute, cancelling...");

            return true;
        }

        if (executionStarted == -1) {
            executionStarted = System.currentTimeMillis();

            crafts.values().forEach((c) -> totalSteps += quantity);
        }

        ++ticks;

        extractInitial();

        if (this.crafts.isEmpty()) {
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
            for (Craft craft : crafts.values()) {
                if (craft instanceof Processing) {
                    updateProcessing((Processing) craft);
                } else {
                    updateCrafting((Crafting) craft);
                }
            }
            for (Craft c : craftsToRemove) {
                crafts.remove(c.getPattern().getChainHashCode());
            }
            craftsToRemove.clear();

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
        Set<Processing> set = itemsToExpect.get(API.instance().getItemStackHashCode(stack));
        if (set != null) {
            for (Processing p : set) {
                if (p.getItemsToReceiveTotal().get(stack) == null) {
                    continue;
                }
                int needed = p.getItemsToReceiveTotal().get(stack).getCount();
                needed -= p.getItemReceivedCount(stack);


                if (needed != 0) {

                    if (needed > size) {
                        needed = size;
                    }

                    if (p.calculateFinished(stack, needed)) {
                        p.setState(ProcessingState.PROCESSED);
                        itemsToExpect.remove(API.instance().getItemStackHashCode(stack), p);
                    }
                    network.getCraftingManager().onTaskChanged();
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
        }

        return size;
    }

    @Override
    public int onTrackedInsert(FluidStack stack, int size) {
        Set<Processing> set = itemsToExpect.get(API.instance().getFluidStackHashCode(stack));
        if (set != null) {
            for (Processing p : set) {
                if (p.getFluidsToReceiveTotal().get(stack) == null) {
                    continue;
                }
                int needed = p.getFluidsToReceiveTotal().get(stack).amount;
                needed -= p.getFluidReceivedCount(stack);

                if (needed != 0) {

                    if (needed > size) {
                        needed = size;
                    }

                    if (p.calculateFinished(stack, needed)) {
                        p.setState(ProcessingState.PROCESSED);
                        fluidsToExpect.remove(API.instance().getFluidStackHashCode(stack), p);
                    }
                    network.getCraftingManager().onTaskChanged();
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

        for (Craft craft : this.crafts.values()) {
            if (craft instanceof Crafting) {
                Crafting crafting = (Crafting) craft;
                if (crafting.getQuantity() != 0) {
                    for (ItemStack receive : crafting.getPattern().getOutputs()) {
                        elements.add(new CraftingMonitorElementItemRender(receive, 0, 0, 0, 0, receive.getCount() * crafting.getQuantity()), false);
                    }

                }
            } else {
                Processing processing = (Processing) craft;
                if (processing.getState() == ProcessingState.PROCESSED) {
                    continue;
                }
                if (processing.getInserted() > 0) {
                    for (ItemStack stack : processing.getDefaultSet()) {
                        ICraftingMonitorElement element = new CraftingMonitorElementItemRender(stack, 0, 0, stack.getCount() * processing.getInserted(), 0, 0);
                        elements.add(element, true);
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

                        elements.add(element, true);
                    }
                }
                for (FluidStack p : processing.getFluidsToPut().getStacks()) {
                    int count = processing.getProcessing(p);
                    if (count != 0) {
                        ICraftingMonitorElement element = new CraftingMonitorElementFluidRender(p, 0, 0, count, 0, 0);
                        elements.add(element, true);
                    }

                }

                for (FluidStack receive : processing.getFluidsToReceiveTotal().getStacks()) {
                    int scheduled = processing.getScheduled(receive);
                    if (scheduled != 0) {
                        ICraftingMonitorElement element = new CraftingMonitorElementFluidRender(receive, 0, 0, 0, scheduled, 0);

                        if (processing.getState() == ProcessingState.MACHINE_DOES_NOT_ACCEPT) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_does_not_accept_fluid");
                        } else if (processing.getState() == ProcessingState.MACHINE_NONE) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.machine_none");
                        } else if (processing.getState() == ProcessingState.LOCKED) {
                            element = new CraftingMonitorElementError(element, "gui.refinedstorage:crafting_monitor.crafter_is_locked");
                        }

                        elements.add(element, true);
                    }
                }
            }
        }


        for (ItemStack stack : this.internalStorage.getStacks()) {
            elements.addStorage(new CraftingMonitorElementItemRender(stack, stack.getCount(), 0, 0, 0, 0));
        }

        for (FluidStack stack : this.internalFluidStorage.getStacks()) {
            elements.addStorage(new CraftingMonitorElementFluidRender(stack, stack.amount, 0, 0, 0, 0));
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
