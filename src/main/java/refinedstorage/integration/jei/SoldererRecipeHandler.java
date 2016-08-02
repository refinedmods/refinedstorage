package refinedstorage.integration.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class SoldererRecipeHandler implements IRecipeHandler<SoldererRecipeWrapper> {
    @Override
    public Class<SoldererRecipeWrapper> getRecipeClass() {
        return SoldererRecipeWrapper.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return SoldererRecipeCategory.ID;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid(@Nonnull SoldererRecipeWrapper recipe) {
        return SoldererRecipeCategory.ID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(SoldererRecipeWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(SoldererRecipeWrapper recipe) {
        return true;
    }
}
