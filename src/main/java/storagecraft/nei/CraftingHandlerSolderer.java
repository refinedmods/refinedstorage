package storagecraft.nei;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import storagecraft.StorageCraft;
import storagecraft.gui.GuiSolderer;
import storagecraft.tile.solderer.ISoldererRecipe;
import storagecraft.tile.solderer.SoldererRegistry;

public class CraftingHandlerSolderer extends TemplateRecipeHandler
{
	public class SoldererRecipe extends CachedRecipe
	{
		private ArrayList<PositionedStack> ingredients = new ArrayList<PositionedStack>();
		private PositionedStack result;

		public SoldererRecipe(ISoldererRecipe recipe)
		{
			int x = 44 - 5;
			int y = 20 - 11;

			for (int i = 0; i < 3; ++i)
			{
				if (recipe.getRow(i) != null)
				{
					this.ingredients.add(new PositionedStack(recipe.getRow(i), x, y));
				}

				y += 18;
			}

			this.result = new PositionedStack(recipe.getResult(), 134 - 5, 38 - 11);
		}

		@Override
		public PositionedStack getResult()
		{
			return result;
		}

		@Override
		public List<PositionedStack> getIngredients()
		{
			return ingredients;
		}
	}

	@Override
	public String getGuiTexture()
	{
		return new ResourceLocation(StorageCraft.ID, "textures/gui/solderer.png").toString();
	}

	@Override
	public String getRecipeName()
	{
		return StatCollector.translateToLocal("gui." + StorageCraft.ID + ":solderer");
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass()
	{
		return GuiSolderer.class;
	}

	@Override
	public void loadCraftingRecipes(ItemStack result)
	{
		ISoldererRecipe recipe = SoldererRegistry.getRecipe(result);

		if (recipe != null)
		{
			arecipes.add(new SoldererRecipe(recipe));
		}
	}
}
