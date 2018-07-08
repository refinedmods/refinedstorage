package com.raoulvdberge.refinedstorage.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeWrapperHollowCover implements IShapedCraftingRecipeWrapper {
    private ItemStack cover;
    private ItemStack hollowCover;

    public RecipeWrapperHollowCover(ItemStack cover, ItemStack hollowCover) {
        this.cover = ItemHandlerHelper.copyStackWithSize(cover, 1);
        this.hollowCover = ItemHandlerHelper.copyStackWithSize(hollowCover, 8);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputs = new ArrayList<>();

        for (int i = 0; i < 9; ++i) {
            if (i == 4) {
                List<ItemStack> wool = new ArrayList<>();

                for (int j = 0; j < 16; ++j) {
                    wool.add(new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, j));
                }

                inputs.add(wool);
            } else {
                inputs.add(Collections.singletonList(cover));
            }
        }

        ingredients.setInputLists(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, hollowCover);
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }
}
