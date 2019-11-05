package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import java.util.*;


public abstract class Craft {
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_ROOT = "Root";
    private static final String NBT_QUANTITY = "Quantity";
    private static final String NBT_Type = "Type";
    private static final String NBT_SET_INDEX = "SetIndex";
    private static final String NBT_ITEM_SETS_TO_USE = "ItemSetsToUse";
    private static final String NBT_SET_COUNTS = "SetCounts";

    private int quantity = 0;
    private boolean root;

    ICraftingPattern pattern;
    private List<ItemStack> defaultSet = new ArrayList<>();
    protected List<List<ItemStack>> itemSetsToUse = new ArrayList<>();
    private List<Integer> itemSetCounts = new ArrayList<>();
    protected int setIndex = 0;

    //these are all irrelevant after calculation is finished
    private List<List<ItemStack>> oredictedItems = new ArrayList<>();
    private List<List<Integer>> oredictCounts = new ArrayList<>();
    private List<Integer> countPerRecipeList = new ArrayList<>();
    private List<Integer> indexList;


    public Craft(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompoundTag(NBT_PATTERN), network.world());
        this.root = tag.getBoolean(NBT_ROOT);
        this.quantity = tag.getInteger(NBT_QUANTITY);
        this.setIndex = tag.getInteger(NBT_SET_INDEX);

        NBTTagList list = tag.getTagList(NBT_ITEM_SETS_TO_USE, Constants.NBT.TAG_LIST);
        for(int i = 0; i<list.tagCount(); i++) {
            NBTTagList innerList = (NBTTagList)list.get(i);
            List<ItemStack> itemList = new ArrayList<>();
            for(int j = 0; j<innerList.tagCount();j++){
                itemList.add(StackUtils.deserializeStackFromNbt(innerList.getCompoundTagAt(j)));
            }
            itemSetsToUse.add(itemList);
        }
        itemSetCounts = Ints.asList(tag.getIntArray(NBT_SET_COUNTS));
        defaultSet = getCurrentSet(); // could save this to NBT instead. very little difference though
    }

    public Craft(ICraftingPattern pattern, boolean root) {
        this.pattern = pattern;
        this.root = root;
    }

    public void finishCalculation() {
        if(!oredictedItems.isEmpty()) {
            convertOredictListsToSets();
        }
        this.defaultSet = getCurrentSet();
    }

    private void convertOredictListsToSets() {
        indexList = new ArrayList<>(Collections.nCopies(oredictCounts.size(), 0));
        int minFullSets = Integer.MAX_VALUE;
        while (indexStillInRange()) {
            for (int ingredient = 0; ingredient < oredictCounts.size(); ingredient++) {
                if (minFullSets > getCountOfIndexed(ingredient) / countPerRecipeList.get(ingredient)) {
                    minFullSets = getCountOfIndexed(ingredient) / countPerRecipeList.get(ingredient);
                }
            }
            if (minFullSets == 0) { // a split set is needed consisting of multiple oredicted items
                itemSetsToUse.add(createSplitSet());
                itemSetCounts.add(1);

            } else {
                itemSetsToUse.add(createSet(minFullSets));
                itemSetCounts.add(minFullSets);
            }
        }
    }

    private List<ItemStack> createSplitSet(){
        List<ItemStack> list = new ArrayList<>();
        for (int ingredient = 0; ingredient < oredictCounts.size(); ingredient++) {
            int count;
            int needed = countPerRecipeList.get(ingredient);
            while (needed > 0) {
                count = getCountOfIndexed(ingredient);
                ItemStack copy = oredictedItems.get(ingredient).get(indexList.get(ingredient)).copy();
                if (count < needed) {
                    oredictCounts.get(ingredient).set(indexList.get(ingredient),0);

                } else {
                    decreaseItemCount(ingredient,needed);
                    count = needed;
                }
                if(getCountOfIndexed(ingredient) == 0){
                    increaseIndex(ingredient);
                }
                copy.setCount(count);
                list.add(copy);
                needed -= count;

            }
        }
        return list;
    }

    private List<ItemStack> createSet(int setCount){
        List<ItemStack> list = new ArrayList<>();
        for (int ingredient = 0; ingredient < oredictCounts.size(); ingredient++) {
            int needed = setCount * countPerRecipeList.get(ingredient);
            decreaseItemCount(ingredient,needed);
            ItemStack copy = oredictedItems.get(ingredient).get(indexList.get(ingredient)).copy();
            copy.setCount(countPerRecipeList.get(ingredient));
            list.add(copy);
            if (getCountOfIndexed(ingredient) == 0) {
                indexList.set(ingredient, indexList.get(ingredient) + 1);
            }


        }
        return list;
    }

    private void decreaseItemCount(int ingredient, int amount){
        oredictCounts.get(ingredient).set(indexList.get(ingredient),
                oredictCounts.get(ingredient).get(indexList.get(ingredient)) - amount);
    }

    private void increaseIndex(int ingredient){
        indexList.set(ingredient, indexList.get(ingredient) + 1);
    }

    private int getCountOfIndexed(int ingredient){
        return oredictCounts.get(ingredient).get(indexList.get(ingredient));
    }

    private boolean indexStillInRange(){
        //index has not surpassed last item
        return indexList.get(0) < oredictCounts.get(0).size();
    }

    public static Craft createCraftFromNBT(INetwork network, NBTTagCompound tag) throws CraftingTaskReadException {
        return tag.getBoolean(NBT_Type) ? new Processing(network, tag) : new Crafting(network, tag);
    }

    public List<ItemStack> getDefaultSet() {
        return defaultSet;
    }
    public void nextSet(){
        quantity--;
        if(itemSetCounts.isEmpty())
            return;
        int count = itemSetCounts.get(setIndex);
        if(count == 0){
            setIndex++;
        } else{
            itemSetCounts.set(setIndex,count -1);
        }

    }


    public List<ItemStack> getCurrentSet() {
        if(itemSetsToUse.isEmpty()){
            return new ArrayList<>();
        }
        return itemSetsToUse.get(setIndex);
    }

    public void addToOredictLists(ItemStack stack, int count, int countInRecipe, int ingredientNumber) {
        List<ItemStack> itemList;
        List<Integer> countList;
        if (oredictedItems.size() > ingredientNumber) { // this type of item was stored before
         itemList = oredictedItems.get(ingredientNumber);
         countList = oredictCounts.get(ingredientNumber);
            for (int i = 0; i < itemList.size(); i++) { //if same item already in list just add to countList
                if (API.instance().getComparer().isEqualNoQuantity(stack, itemList.get(i))) {
                    countList.set(i, countList.get(i) + count);
                    return;
                }
            } //if not found its a new oredict item of the same type just add it
            itemList.add(stack);
            countList.add(count);
        } else { //if not stored before create new
            itemList = new ArrayList<>();
            countList = new ArrayList<>();
            itemList.add(stack);
            countList.add(count);
            countPerRecipeList.add(countInRecipe);
            oredictCounts.add(countList);
            oredictedItems.add(itemList);
        }

    }

    public void addQuantity(int amount) {
        quantity += amount;
    }

    public int getQuantity() {
        return quantity;
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
        for (List<ItemStack> stacks : itemSetsToUse) {
            NBTTagList stacklist = new NBTTagList();
            for(ItemStack stack : stacks){
                stacklist.appendTag(StackUtils.serializeStackToNbt(stack));
            }
            list.appendTag(stacklist);
        }
        tag.setTag(NBT_ITEM_SETS_TO_USE, list);
        NBTTagIntArray countList = new NBTTagIntArray(itemSetCounts);
        tag.setTag(NBT_SET_COUNTS,countList);
        tag.setInteger(NBT_SET_INDEX,setIndex);
        return tag;
    }
}

