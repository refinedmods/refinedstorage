package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;


import java.util.*;


public abstract class Craft {
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_ROOT = "Root";
    private static final String NBT_QUANTITY = "Quantity";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_ITEMS_TO_USE = "ItemsToUse";
    private static final String NBT_FLUIDS_TO_USE = "FluidsToUse";
    private static final String NBT_INGREDIENT_NUMBER = "IngredientNumber";
    private static final String NBT_NUMBER_STORED = "NumberStored";
    private static final String NBT_STACK_STORED = "StackStored";
    private static final String NBT_TAG_STORAGE = "TagStorage";
    private static final String NBT_COUNT_PER_RECIPE = "CountPerRecipe";
    private static final String NBT_DEFAULT_ITEM_SET = "DefaultItemSet";
    private static final String NBT_DEFAULT_FLUID_SET = "DefaultFluidSet";

    private int quantity = 0;
    private boolean root;

    ICraftingPattern pattern;
    private Collection<ItemStack> defaultItemSet = new ArrayList<>();
    private Collection<FluidStack> defaultFluidSet = new ArrayList<>();
    private Map<Integer, ItemTagStorage> itemsToUse = new LinkedHashMap<>();
    private Map<Integer, FluidTagStorage> fluidsToUse = new LinkedHashMap<>();

    public Craft(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompound(NBT_PATTERN), network.getWorld());
        this.root = tag.getBoolean(NBT_ROOT);
        this.quantity = tag.getInt(NBT_QUANTITY);

        ListNBT itemList = tag.getList(NBT_ITEMS_TO_USE, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < itemList.size(); i++) {
            itemsToUse.put(itemList.getCompound(i).getInt(NBT_INGREDIENT_NUMBER), new ItemTagStorage(itemList.getCompound(i)));
        }
        ListNBT fluidList = tag.getList(NBT_FLUIDS_TO_USE, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < fluidList.size(); i++) {
            fluidsToUse.put(fluidList.getCompound(i).getInt(NBT_INGREDIENT_NUMBER), new FluidTagStorage(fluidList.getCompound(i)));
        }
        ListNBT defaultItems = tag.getList(NBT_DEFAULT_ITEM_SET, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i< defaultItems.size(); i++) {
            defaultItemSet.add(StackUtils.deserializeStackFromNbt(defaultItems.getCompound(i)));
        }
        ListNBT defaultFluids = tag.getList(NBT_DEFAULT_FLUID_SET, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i< defaultFluids.size(); i++) {
            defaultFluidSet.add(FluidStack.loadFluidStackFromNBT(defaultFluids.getCompound(i)));
        }
    }

    public Craft(ICraftingPattern pattern, boolean root) {
        this.pattern = pattern;
        this.root = root;
    }

    public static Craft createCraftFromNBT(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        return tag.getBoolean(NBT_TYPE) ? new Processing(network, tag) : new Crafting(network, tag);
    }

    public void finishCalculation() {
        this.defaultItemSet = getNextItemSet(Action.SIMULATE);
        this.defaultFluidSet = getNextFluidSet(Action.SIMULATE);
    }

    public Collection<ItemStack> getDefaultItemSet() {
        return defaultItemSet;
    }

    public Collection<FluidStack> getDefaultFluidSet() {
        return defaultFluidSet;
    }


    public Collection<ItemStack> getNextItemSet(Action action) {
        List<ItemStack> stacks = new ArrayList<>();
        for (ItemTagStorage storage : itemsToUse.values()) {
            int count = storage.getCountPerRecipe();
            int taken = 0;
            int type  = 0;
            while (count > taken) {
                ItemStack stack = storage.getItem(type, count, action == Action.SIMULATE);
                stacks.add(stack);
                taken += stack.getCount();
                type++;
            }
        }
        return stacks;
    }

    public Collection<FluidStack> getNextFluidSet(Action action) {
        List<FluidStack> stacks = new ArrayList<>();
        for (FluidTagStorage storage : fluidsToUse.values()) {
            int count = storage.getCountPerRecipe();
            int taken = 0;
            int type = 0;
            while (count > taken) {
                FluidStack stack = storage.getFluid(type, count, action == Action.SIMULATE);
                stacks.add(stack);
                taken += stack.getAmount();
                type++;
            }
        }
        return stacks;
    }

    public void addToTagStorage(ItemStack stack, int count, int countInRecipe, int ingredientNumber) {
        ItemTagStorage storage = itemsToUse.get(ingredientNumber);
        if (storage == null) {
            storage = new ItemTagStorage(countInRecipe);
            itemsToUse.put(ingredientNumber, storage);
        }
        storage.add(stack, count);
    }

    public void addToTagStorage(FluidStack stack, int count, int countInRecipe, int ingredientNumber) {
        FluidTagStorage storage = fluidsToUse.get(ingredientNumber);
        if (storage == null) {
            storage = new FluidTagStorage(countInRecipe);
            fluidsToUse.put(ingredientNumber, storage);
        }
        storage.add(stack, count);
    }

    public void addQuantity(int amount) {
        quantity += amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void reduceQuantity() {
        quantity--;
    }

    public ICraftingPattern getPattern() {
        return pattern;
    }

    boolean isRoot() {
        return root;
    }

    public CompoundNBT writeToNbt() {
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean(NBT_TYPE, this instanceof Processing);
        tag.putBoolean(NBT_ROOT, root);
        tag.putInt(NBT_QUANTITY, quantity);
        tag.put(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        ListNBT itemList = new ListNBT();
        for (Map.Entry<Integer, ItemTagStorage> entry : itemsToUse.entrySet()) {
            CompoundNBT compound = new CompoundNBT();
            compound.putInt(NBT_INGREDIENT_NUMBER, entry.getKey());
            entry.getValue().writeToNbt(compound);
            itemList.add(compound);
        }
        tag.put(NBT_ITEMS_TO_USE, itemList);

        ListNBT fluidList = new ListNBT();
        for (Map.Entry<Integer, FluidTagStorage> entry : fluidsToUse.entrySet()) {
            CompoundNBT compound = new CompoundNBT();
            compound.putInt(NBT_INGREDIENT_NUMBER, entry.getKey());
            entry.getValue().writeToNbt(compound);
            fluidList.add(compound);
        }
        tag.put(NBT_FLUIDS_TO_USE, fluidList);

        ListNBT defaultItems = new ListNBT();
        for(ItemStack stack : defaultItemSet){
            defaultItems.add(StackUtils.serializeStackToNbt(stack));
        }
        tag.put(NBT_DEFAULT_ITEM_SET, defaultItems);

        ListNBT defaultFluids = new ListNBT();
        for(FluidStack stack : defaultFluidSet){
            CompoundNBT nbt = new CompoundNBT();
            defaultFluids.add(stack.writeToNBT(nbt));
        }
        tag.put(NBT_DEFAULT_FLUID_SET, defaultFluids);



        return tag;
    }

    static class ItemTagStorage {

        private int countPerRecipe;
        LinkedList<Pair<ItemStack, Integer>> storedItems = new LinkedList<>();

        public void writeToNbt(CompoundNBT compound) {
            compound.putInt(NBT_COUNT_PER_RECIPE, countPerRecipe);
            ListNBT itemList = new ListNBT();
            for (Pair<ItemStack, Integer> pair : storedItems) {
                CompoundNBT compound2 = new CompoundNBT();
                compound2.putInt(NBT_NUMBER_STORED, pair.getValue());
                compound2.put(NBT_STACK_STORED, StackUtils.serializeStackToNbt(pair.getKey()));
                itemList.add(compound2);
            }
            compound.put(NBT_TAG_STORAGE, itemList);
        }

        int getCountPerRecipe() {
            return countPerRecipe;
        }

        ItemTagStorage(int countPerRecipe) {
            this.countPerRecipe = countPerRecipe;
        }

        ItemTagStorage(CompoundNBT compound) {
            this.countPerRecipe = compound.getInt(NBT_COUNT_PER_RECIPE);
            ListNBT itemList = compound.getList(NBT_TAG_STORAGE, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < itemList.size(); i++) {
                CompoundNBT nbt = itemList.getCompound(i);
                storedItems.add(new MutablePair<>(StackUtils.deserializeStackFromNbt(nbt.getCompound(NBT_STACK_STORED)), nbt.getInt(NBT_NUMBER_STORED)));
            }
        }

        public void add(ItemStack stack, int count) {
            for (Pair<ItemStack, Integer> pair : storedItems) {
                if (API.instance().getComparer().isEqualNoQuantity(stack, pair.getLeft())) {
                    pair.setValue(pair.getRight() + count);
                    return;
                }
            }
            storedItems.add(new MutablePair<>(stack, count));
        }

        public ItemStack getItem(int type, int needed, boolean simulate) {
            if (storedItems.isEmpty()) {
                throw new IllegalStateException("Craft Requested More items than available");
            }
            //Index may move if not simulating
            Pair<ItemStack, Integer> pair = simulate ? storedItems.get(type) : storedItems.peek();
            int contained = pair.getRight();
            ItemStack toReturn = pair.getLeft().copy();
            if (needed < contained) {
                if (!simulate) {
                    pair.setValue(contained - needed);
                }
                toReturn.setCount(needed);
            } else {
                if (!simulate) {
                    storedItems.remove();
                }
                toReturn.setCount(contained);
            }
            return toReturn;
        }
    }

    static class FluidTagStorage {

        private int countPerRecipe;
        LinkedList<Pair<FluidStack, Integer>> storedFluids = new LinkedList<>();

        public void writeToNbt(CompoundNBT compound) {
            compound.putInt(NBT_COUNT_PER_RECIPE, countPerRecipe);

            ListNBT fluidList = new ListNBT();
            for (Pair<FluidStack, Integer> pair : storedFluids) {
                CompoundNBT compound2 = new CompoundNBT();
                compound2.putInt(NBT_NUMBER_STORED, pair.getRight());
                compound2.put(NBT_STACK_STORED, pair.getLeft().writeToNBT(compound2));
                fluidList.add(compound2);
            }
            compound.put(NBT_TAG_STORAGE, fluidList);
        }


        int getCountPerRecipe() {
            return countPerRecipe;
        }

        FluidTagStorage(int countPerRecipe) {
            this.countPerRecipe = countPerRecipe;
        }

        FluidTagStorage(CompoundNBT compound) {
            this.countPerRecipe = compound.getInt(NBT_COUNT_PER_RECIPE);
            ListNBT fluidList = compound.getList(NBT_FLUIDS_TO_USE, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < fluidList.size(); i++) {
                CompoundNBT nbt = fluidList.getCompound(i);
                storedFluids.add(new MutablePair<>(FluidStack.loadFluidStackFromNBT(nbt.getCompound(NBT_STACK_STORED)), nbt.getInt(NBT_NUMBER_STORED)));
            }
        }

        public void add(FluidStack stack, int count) {
            for (Pair<FluidStack, Integer> pair : storedFluids) {
                if (stack.getFluid() == pair.getLeft().getFluid() && FluidStack.areFluidStackTagsEqual(stack, pair.getLeft())) {
                    pair.setValue(pair.getRight() + count);
                    return;
                }
            }
            storedFluids.add(new MutablePair<>(stack, count));
        }

        public FluidStack getFluid(int type, int needed, boolean simulate) {
            if (storedFluids.isEmpty()) {
                throw new IllegalStateException("Craft Requested More Fluids than available");
            }
            //Index may move if not simulating
            Pair<FluidStack, Integer> pair = simulate ? storedFluids.get(type) : storedFluids.peek();
            int contained = pair.getRight();
            FluidStack toReturn = pair.getLeft().copy();
            if (needed < contained) {
                if(!simulate) {
                    pair.setValue(contained - needed);
                }
                toReturn.setAmount(needed);
            } else {
                if(!simulate) {
                    storedFluids.remove();
                }
                toReturn.setAmount(contained);
            }
            return toReturn;
        }
    }
}

