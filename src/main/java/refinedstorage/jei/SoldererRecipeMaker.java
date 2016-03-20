package refinedstorage.jei;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import refinedstorage.tile.solderer.ISoldererRecipe;
import refinedstorage.tile.solderer.SoldererRegistry;

public class SoldererRecipeMaker
{
	public static List<SoldererRecipeWrapper> getRecipes()
	{
		List<SoldererRecipeWrapper> recipes = new ArrayList<SoldererRecipeWrapper>();

		for (ISoldererRecipe recipe : SoldererRegistry.recipes)
		{
			List<ItemStack> inputs = new ArrayList<ItemStack>();

			inputs.add(recipe.getRow(0));
			inputs.add(recipe.getRow(1));
			inputs.add(recipe.getRow(2));

			ItemStack output = recipe.getResult();

			recipes.add(new SoldererRecipeWrapper(inputs, output));
		}

		return recipes;
	}
}
