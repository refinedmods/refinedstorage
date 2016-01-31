package storagecraft.tile.solderer;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import storagecraft.util.InventoryUtils;

public class SoldererRegistry
{
	public static List<ISoldererRecipe> recipes = new ArrayList<ISoldererRecipe>();

	public static void addRecipe(ISoldererRecipe recipe)
	{
		recipes.add(recipe);
	}

	public static ISoldererRecipe getRecipe(IInventory inventory)
	{
		for (ISoldererRecipe recipe : recipes)
		{
			boolean ok = true;

			for (int i = 0; i < 3; ++i)
			{
				if (!InventoryUtils.compareStackNoQuantity(recipe.getRow(i), inventory.getStackInSlot(i)))
				{
					ok = false;
				}

				if (inventory.getStackInSlot(i) != null && recipe.getRow(i) != null)
				{
					if (inventory.getStackInSlot(i).stackSize < recipe.getRow(i).stackSize)
					{
						ok = false;
					}
				}
			}

			if (ok)
			{
				return recipe;
			}
		}

		return null;
	}

	public static ISoldererRecipe getRecipe(ItemStack result)
	{
		for (ISoldererRecipe recipe : recipes)
		{
			if (InventoryUtils.compareStack(result, recipe.getResult()))
			{
				return recipe;
			}
		}

		return null;
	}
}
