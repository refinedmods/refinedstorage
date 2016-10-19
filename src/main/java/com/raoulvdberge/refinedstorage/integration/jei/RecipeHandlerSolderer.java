package com.raoulvdberge.refinedstorage.integration.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class RecipeHandlerSolderer implements IRecipeHandler<RecipeWrapperSolderer> {
    @Override
    @Nonnull
    public Class<RecipeWrapperSolderer> getRecipeClass() {
        return RecipeWrapperSolderer.class;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public String getRecipeCategoryUid() {
        return RecipeCategorySolderer.ID;
    }

    @Override
    @Nonnull
    public String getRecipeCategoryUid(@Nonnull RecipeWrapperSolderer recipe) {
        return RecipeCategorySolderer.ID;
    }

    @Override
    @Nonnull
    public IRecipeWrapper getRecipeWrapper(@Nonnull RecipeWrapperSolderer recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nonnull RecipeWrapperSolderer recipe) {
        return true;
    }
}
