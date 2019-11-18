package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.api.util.StackListEntry;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public abstract class Craft {
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_ROOT = "Root";
    private static final String NBT_IS_PROCESSING = "IsProcessing";
    private static final String NBT_ITEMS_TO_USE = "ItemsToUse";
    private static final String NBT_QUANTITY = "Quantity";
    private static final String NBT_NEEDED_PER_CRAFT = "NeededPerCraft";

    private boolean root;
    protected int quantity;
    private ICraftingPattern pattern;
    private List<IStackList<ItemStack>> itemsToUse = new ArrayList<>();
    private List<Integer> neededPerCraft = new ArrayList<>();

    public Craft(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        this.quantity = tag.getInt(NBT_QUANTITY);
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompound(NBT_PATTERN), network.getWorld());
        this.root = tag.getBoolean(NBT_ROOT);
        ListNBT list = tag.getList(NBT_ITEMS_TO_USE, Constants.NBT.TAG_LIST);
        for (int i = 0; i < list.size(); i++) {
            this.itemsToUse.add(CraftingTask.readItemStackList(list.getList(i)));
        }
        this.neededPerCraft = Ints.asList(tag.getIntArray(NBT_NEEDED_PER_CRAFT));
    }

    public Craft(int quantity, ICraftingPattern pattern, boolean root) {
        this.quantity = quantity;
        this.pattern = pattern;
        this.root = root;
    }

    public static Craft createCraftFromNBT(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        return tag.getBoolean(NBT_IS_PROCESSING) ? new Processing(network, tag) : new Crafting(network, tag);
    }

    public ICraftingPattern getPattern() {
        return pattern;
    }

    public int getQuantity() {
        return quantity;
    }

    public void next() {
        quantity--;
    }

    boolean isRoot() {
        return root;
    }

    boolean hasItems() {
        return !itemsToUse.isEmpty();
    }

    IStackList<ItemStack> getItemsToUse(boolean simulate) {
        IStackList<ItemStack> toReturn = API.instance().createItemStackList();
        for (int i = 0; i < itemsToUse.size(); i++) {
            int needed = neededPerCraft.get(i);
            if (!itemsToUse.get(i).isEmpty()) {
                Iterator<StackListEntry<ItemStack>> it = itemsToUse.get(i).getStacks().iterator();
                while (needed > 0 && it.hasNext()) {
                    ItemStack toUse = it.next().getStack();
                    if (needed < toUse.getCount()) {
                        if (!simulate) {
                            itemsToUse.get(i).remove(toUse, needed);
                        }
                        toReturn.add(toUse, needed);
                        needed = 0;
                    } else {
                        if (!simulate) {
                            it.remove();
                        }
                        needed -= toUse.getCount();
                        toReturn.add(toUse);
                    }
                }
            } else {
                LogManager.getLogger(Craft.class).warn("Craft requested more Items than available");
                this.quantity = 0; // stop crafting
                break;
            }
        }
        return toReturn;
    }

    public void addItemsToUse(int ingredientNumber, ItemStack stack, int size, int perCraft) {
        if (neededPerCraft.size() <= ingredientNumber) {
            neededPerCraft.add(perCraft);
        }
        if (itemsToUse.size() <= ingredientNumber) {
            itemsToUse.add(API.instance().createItemStackList());
        }
        itemsToUse.get(ingredientNumber).add(stack, size);
    }

    public CompoundNBT writeToNbt() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(NBT_QUANTITY, quantity);
        tag.putBoolean(NBT_IS_PROCESSING, this instanceof Processing);
        tag.putBoolean(NBT_ROOT, root);
        tag.put(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        ListNBT list = new ListNBT();
        for (IStackList<ItemStack> stackList : itemsToUse) {
            list.add(CraftingTask.writeItemStackList(stackList));
        }
        tag.put(NBT_ITEMS_TO_USE, list);
        tag.putIntArray(NBT_NEEDED_PER_CRAFT, Ints.toArray(neededPerCraft));


        return tag;
    }

}
