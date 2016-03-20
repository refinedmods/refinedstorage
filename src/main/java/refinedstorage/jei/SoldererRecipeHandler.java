package refinedstorage.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class SoldererRecipeHandler implements IRecipeHandler<SoldererRecipeWrapper>
{
	@Override
	public Class<SoldererRecipeWrapper> getRecipeClass()
	{
		return SoldererRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid()
	{
		return "refinedstorage.solderer";
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(SoldererRecipeWrapper recipe)
	{
		return recipe;
	}

	@Override
	public boolean isRecipeValid(SoldererRecipeWrapper recipe)
	{
		return true;
	}
}
