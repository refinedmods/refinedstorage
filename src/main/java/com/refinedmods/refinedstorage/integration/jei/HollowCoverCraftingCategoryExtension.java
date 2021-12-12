package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.item.CoverItem;
import com.refinedmods.refinedstorage.recipe.HollowCoverRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICustomCraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Size2i;

import javax.annotation.Nullable;

public class HollowCoverCraftingCategoryExtension implements ICustomCraftingCategoryExtension {

    @Override
    public void setIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, new ItemStack(RSItems.COVER.get()));
        ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(RSItems.HOLLOW_COVER.get()));
    }

    @Nullable
    @Override
    public Size2i getSize() {
        return new Size2i(2, 1);
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return HollowCoverRecipe.SERIALIZER.getRegistryName();
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
        ItemStack stack = recipeLayout.getFocus(VanillaTypes.ITEM).getValue();
        if (stack.getItem() == RSItems.COVER.get()) {
            recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(5, stack);
            ItemStack output = new ItemStack(RSItems.HOLLOW_COVER.get());
            CoverItem.setItem(output, CoverItem.getItem(stack));
            recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(0, output);
        } else {
            ItemStack input = new ItemStack(RSItems.COVER.get());
            CoverItem.setItem(input, CoverItem.getItem(stack));
            recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(5, input);
            recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).set(0, stack);
        }
    }
}
