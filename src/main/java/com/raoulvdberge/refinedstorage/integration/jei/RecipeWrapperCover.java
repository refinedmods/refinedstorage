package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.RSItems;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class RecipeWrapperCover implements ICraftingRecipeWrapper {
    private ItemStack stack;
    private ItemStack cover;

    public RecipeWrapperCover(ItemStack stack, ItemStack cover) {
        this.stack = ItemHandlerHelper.copyStackWithSize(stack, 1);
        this.cover = ItemHandlerHelper.copyStackWithSize(cover, 6);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> inputs = new ArrayList<>();

        inputs.add(new ItemStack(RSItems.CUTTING_TOOL));
        inputs.add(stack);

        ingredients.setInputs(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, cover);
    }
}
