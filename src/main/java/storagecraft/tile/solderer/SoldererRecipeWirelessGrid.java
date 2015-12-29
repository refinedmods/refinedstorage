package storagecraft.tile.solderer;

import net.minecraft.item.ItemStack;
import storagecraft.StorageCraftBlocks;
import storagecraft.StorageCraftItems;
import storagecraft.item.ItemProcessor;

public class SoldererRecipeWirelessGrid implements ISoldererRecipe
{
	private int type;

	public SoldererRecipeWirelessGrid(int type)
	{
		this.type = type;
	}

	@Override
	public ItemStack getRow(int row)
	{
		if (row == 0)
		{
			return new ItemStack(StorageCraftItems.WIRELESS_GRID_PLATE);
		}
		else if (row == 1)
		{
			return new ItemStack(StorageCraftBlocks.GRID, 1, type);
		}
		else if (row == 2)
		{
			return new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED);
		}

		return null;
	}

	@Override
	public ItemStack getResult()
	{
		return new ItemStack(StorageCraftItems.WIRELESS_GRID, 1, type);
	}

	@Override
	public int getDuration()
	{
		return 1000;
	}
}
