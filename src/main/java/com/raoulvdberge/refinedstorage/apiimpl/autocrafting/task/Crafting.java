package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

class Crafting extends Craft {
    private static final String NBT_TOOK = "Took";
    private NonNullList<ItemStack> took;

    public Crafting(ICraftingPattern pattern, NonNullList<ItemStack> took, IStackList<ItemStack> itemsToUse, boolean root) {
        super(pattern, root, itemsToUse);
        this.took = took;
    }

    public Crafting(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        super(network, tag);
        this.took = NonNullList.create();
        ListNBT tookList = tag.getList(NBT_TOOK, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tookList.size(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(tookList.getCompound(i));

            // Can be empty.
            took.add(stack);
        }
    }

    public NonNullList<ItemStack> getTook() {
        return took;
    }

    public CompoundNBT writeToNbt() {
        CompoundNBT tag = super.writeToNbt();

        ListNBT tookList = new ListNBT();
        for (ItemStack took : this.took) {
            tookList.add(StackUtils.serializeStackToNbt(took));
        }

        tag.put(NBT_TOOK, tookList);

        return tag;
    }
}
