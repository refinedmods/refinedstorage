package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;


public abstract class Craft {
    private static final String NBT_PATTERN = "Pattern";
    private static final String NBT_ROOT = "Root";
    private static final String NBT_IS_PROCESSING = "IsProcessing";
    private static final String NBT_ITEMS_TO_USE = "ItemsToUse";

    private boolean root;
    private ICraftingPattern pattern;
    private IStackList<ItemStack> itemsToUse;

    public Craft(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        this.pattern = CraftingTask.readPatternFromNbt(tag.getCompound(NBT_PATTERN), network.getWorld());
        this.root = tag.getBoolean(NBT_ROOT);
        this.itemsToUse = CraftingTask.readItemStackList(tag.getList(NBT_ITEMS_TO_USE, Constants.NBT.TAG_COMPOUND));
    }

    public Craft(ICraftingPattern pattern, boolean root, IStackList<ItemStack> itemsToUse) {
        this.pattern = pattern;
        this.root = root;
        this.itemsToUse = itemsToUse;
    }

    public static Craft createCraftFromNBT(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        return tag.getBoolean(NBT_IS_PROCESSING) ? new Processing(network, tag) : new Crafting(network, tag);
    }

    public ICraftingPattern getPattern() {
        return pattern;
    }

    boolean isRoot() {
        return root;
    }

    IStackList<ItemStack> getItemsToUse() {
        return itemsToUse;
    }

    public CompoundNBT writeToNbt() {
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean(NBT_IS_PROCESSING, this instanceof Processing);
        tag.putBoolean(NBT_ROOT, root);
        tag.put(NBT_PATTERN, CraftingTask.writePatternToNbt(pattern));
        tag.put(NBT_ITEMS_TO_USE, CraftingTask.writeItemStackList(itemsToUse));
        return tag;
    }

}
