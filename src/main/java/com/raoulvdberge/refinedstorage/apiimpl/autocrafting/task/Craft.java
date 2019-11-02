package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;


import java.util.*;


public abstract class Craft {
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_ROOT = "Root";
    private static final String NBT_QUANTITY = "Quantity";
    private static final String NBT_Type = "Type";
    private static final String NBT_INGREDIENT_NUMBER = "IngredientNumber";
    private static final String NBT_COUNT_PER_RECIPE = "CountPerRecipe";
    private static final String NBT_ITEMS_TO_USE = "ItemsToUse";
    private static final String NBT_NUMBER_STORED = "NumberStored";
    private static final String NBT_ITEM_STORED = "ItemStored";
    private static final String NBT_OREDICT_ITEM_LIST = "OredictItemList";

    private int quantity = 0;
    private boolean root;

    ICraftingPattern pattern;
    private List<ItemStack> defaultSet = new ArrayList<>();
    private Map<Integer, oreDictStorage> itemsToUse = new LinkedHashMap<>();

    public Craft(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompoundTag(NBT_PATTERN), network.world());
        this.root = tag.getBoolean(NBT_ROOT);
        this.quantity = tag.getInteger(NBT_QUANTITY);

        NBTTagList list = tag.getTagList(NBT_ITEMS_TO_USE, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            int countInRecipe = list.getCompoundTagAt(i).getInteger(NBT_COUNT_PER_RECIPE);
            int ingredientNumber = list.getCompoundTagAt(i).getInteger(NBT_INGREDIENT_NUMBER);
            itemsToUse.put(ingredientNumber, new oreDictStorage(countInRecipe));
            NBTTagList list2 = list.getCompoundTagAt(i).getTagList(NBT_OREDICT_ITEM_LIST, Constants.NBT.TAG_COMPOUND);
            for (int j = 0; j < list2.tagCount(); j++) {
                NBTTagCompound compound = list2.getCompoundTagAt(j);
                addToItemSets(StackUtils.deserializeStackFromNbt(compound.getCompoundTag(NBT_ITEM_STORED)), compound.getInteger(NBT_NUMBER_STORED), countInRecipe, ingredientNumber);
            }
        }
        defaultSet = getDefaultSet();
    }

    public Craft(ICraftingPattern pattern, boolean root) {
        this.pattern = pattern;
        this.root = root;
    }

    public void finishCalculation() {
        this.defaultSet = getNextSet(Action.SIMULATE);
    }

    public static Craft createCraftFromNBT(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        return tag.getBoolean(NBT_Type) ? new Processing(network, tag) : new Crafting(network, tag);
    }

    public List<ItemStack> getDefaultSet() {
        return defaultSet;
    }


    public List<ItemStack> getNextSet(Action action) {
        List<ItemStack> stacks = new ArrayList<>();
        for (oreDictStorage storage : itemsToUse.values()) {
            int count = storage.getCountPerRecipe();
            int taken = 0;
            while (count > taken) {
                ItemStack stack = storage.get(count, action == Action.SIMULATE);
                stacks.add(stack);
                taken += stack.getCount();
            }
        }
        return stacks;
    }

    public void addToItemSets(ItemStack stack, int count, int countInRecipe, int ingredientNumber) {
        oreDictStorage storage = itemsToUse.get(ingredientNumber);
        if (storage == null) {
            storage = new oreDictStorage(countInRecipe);
            itemsToUse.put(ingredientNumber, storage);
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

    public NBTTagCompound writeToNbt() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(NBT_Type, this instanceof Processing);
        tag.setBoolean(NBT_ROOT, root);
        tag.setInteger(NBT_QUANTITY, quantity);
        tag.setTag(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        NBTTagList list = new NBTTagList();
        for (Map.Entry<Integer, oreDictStorage> entry : itemsToUse.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger(NBT_INGREDIENT_NUMBER, entry.getKey());
            entry.getValue().writeToNBT(compound);
            list.appendTag(compound);
        }
        tag.setTag(NBT_ITEMS_TO_USE, list);
        return tag;
    }

    static class oreDictStorage {
        int countPerRecipe;
        Queue<Pair<ItemStack,Integer>> stored = new LinkedList<>();

        public void writeToNBT(NBTTagCompound compound) {
            compound.setInteger(NBT_COUNT_PER_RECIPE, countPerRecipe);
            NBTTagList list = new NBTTagList();
            for (Pair<ItemStack,Integer>  pair : stored) {
                NBTTagCompound compound2 = new NBTTagCompound();
                compound2.setInteger(NBT_NUMBER_STORED, pair.getValue());
                compound2.setTag(NBT_ITEM_STORED, StackUtils.serializeStackToNbt(pair.getKey()));
                list.appendTag(compound2);
            }
            compound.setTag(NBT_OREDICT_ITEM_LIST, list);
        }

        public int getCountPerRecipe() {
            return countPerRecipe;
        }

        oreDictStorage(int countPerRecipe) {
            this.countPerRecipe = countPerRecipe;
        }

        public void add(ItemStack stack, int count) {
            boolean exists = false;
            for( Pair<ItemStack,Integer> pair: stored){
                if(API.instance().getComparer().isEqualNoQuantity(stack, pair.getLeft())){
                    pair.setValue(pair.getRight()+count);
                    exists = true;
                }
            }
            if(!exists){
                stored.add(new MutablePair<>(stack,count));
            }
        }

        public ItemStack get(int needed, boolean simulate) {
            if (stored.isEmpty()) {
                throw new IllegalStateException("Craft Requested More items than available");
            }
            Pair<ItemStack,Integer> pair = stored.peek();
            int contained = pair.getRight();
            ItemStack toReturn = pair.getLeft().copy();
            if (needed < contained) {
                if (!simulate) {
                    pair.setValue(contained-needed);
                }
                toReturn.setCount(needed);
            } else {
                if (!simulate) {
                    stored.remove();
                }
                toReturn.setCount(contained);
            }
            return toReturn;
        }
    }
}

