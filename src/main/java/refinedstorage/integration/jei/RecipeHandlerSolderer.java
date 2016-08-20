package refinedstorage.integration.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class RecipeHandlerSolderer implements IRecipeHandler<RecipeWrapperSolderer> {
    @Override
    public Class<RecipeWrapperSolderer> getRecipeClass() {
        return RecipeWrapperSolderer.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return RecipeCategorySolderer.ID;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid(@Nonnull RecipeWrapperSolderer recipe) {
        return RecipeCategorySolderer.ID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(RecipeWrapperSolderer recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(RecipeWrapperSolderer recipe) {
        return true;
    }
}
