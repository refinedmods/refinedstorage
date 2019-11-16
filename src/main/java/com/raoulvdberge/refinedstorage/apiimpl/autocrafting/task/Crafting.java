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
    private static final String NBT_RECIPE = "Recipe";
    private NonNullList<ItemStack> recipe;

    public Crafting(int quantity, ICraftingPattern pattern, NonNullList<ItemStack> recipe, IStackList<ItemStack> itemsToUse, boolean root) {
        super(quantity, pattern, root, itemsToUse);
        this.recipe = recipe;
    }

    public Crafting(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        super(network, tag);
        this.recipe = NonNullList.create();
        ListNBT tookList = tag.getList(NBT_RECIPE, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tookList.size(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(tookList.getCompound(i));

            // Can be empty.
            recipe.add(stack);
        }
    }

    public NonNullList<ItemStack> getRecipe() {
        return recipe;
    }

    public CompoundNBT writeToNbt() {
        CompoundNBT tag = super.writeToNbt();

        ListNBT tookList = new ListNBT();
        for (ItemStack took : this.recipe) {
            tookList.add(StackUtils.serializeStackToNbt(took));
        }

        tag.put(NBT_RECIPE, tookList);

        return tag;
    }
}
