package com.raoulvdberge.refinedstorage.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
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
        List<ItemStack> inputs = new ArrayList<>();

        for (int i = 0; i < 9; ++i) {
            if (i == 4) {
                inputs.add(ItemStack.EMPTY);
            } else {
                inputs.add(cover);
            }
        }

        ingredients.setInputs(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, hollowCover);
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
