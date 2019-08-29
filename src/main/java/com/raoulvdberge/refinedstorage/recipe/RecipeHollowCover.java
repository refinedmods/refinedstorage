package com.raoulvdberge.refinedstorage.recipe;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.item.ItemCover;
import com.raoulvdberge.refinedstorage.item.ItemHollowCover;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RecipeHollowCover implements IRecipe<CraftingInventory> {
    protected boolean isValid(ItemStack slot, @Nullable ItemStack previousValidSlot) {
        ItemStack currentCover = ItemCover.getItem(slot);

        if (slot.getItem() == RSItems.COVER && CoverManager.isValidCover(currentCover)) {
            if (previousValidSlot == null) {
                return true;
            }

            ItemStack previousCover = ItemCover.getItem(previousValidSlot);

            return previousCover.getItem() == currentCover.getItem();
        }

        return false;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        ItemStack previousValidSlot = null;

        for (int i = 0; i < 9; ++i) {
            ItemStack slot = inv.getStackInSlot(i);

            if (i == 4) {
                if (!slot.isEmpty()) {
                    return false;
                }
            } else {
                if (isValid(slot, previousValidSlot)) {
                    previousValidSlot = slot;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack stack = new ItemStack(RSItems.HOLLOW_COVER, 8);

        ItemHollowCover.setItem(stack, ItemCover.getItem(inv.getStackInSlot(0)));

        return stack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(RS.ID, "hollow_cover");
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public IRecipeType<?> getType() {
        return IRecipeType.CRAFTING;
    }
}
