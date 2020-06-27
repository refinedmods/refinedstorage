package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;

class Crafting extends Craft {
    private static final String NBT_RECIPE = "Recipe";
    private final NonNullList<ItemStack> recipe;

    Crafting(ICraftingPattern pattern, boolean root, NonNullList<ItemStack> recipe) {
        super(pattern, root);
        this.recipe = recipe;
    }

    Crafting(INetwork network, CompoundNBT tag) throws CraftingTaskReadException {
        super(network, tag);
        this.recipe = NonNullList.create();
        ListNBT tookList = tag.getList(NBT_RECIPE, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tookList.size(); ++i) {
            ItemStack stack = StackUtils.deserializeStackFromNbt(tookList.getCompound(i));

            // Can be empty.
            recipe.add(stack);
        }
    }

    NonNullList<ItemStack> getRecipe() {
        return recipe;
    }

    CompoundNBT writeToNbt() {
        CompoundNBT tag = super.writeToNbt();

        ListNBT tookList = new ListNBT();
        for (ItemStack took : this.recipe) {
            tookList.add(StackUtils.serializeStackToNbt(took));
        }

        tag.put(NBT_RECIPE, tookList);

        return tag;
    }
}
