package storagecraft.tile.solderer;

import net.minecraft.item.ItemStack;
import storagecraft.StorageCraftBlocks;
import storagecraft.StorageCraftItems;
import storagecraft.block.EnumGridType;
import storagecraft.item.ItemProcessor;

public class SoldererRecipePatternGrid implements ISoldererRecipe
{
	@Override
	public ItemStack getRow(int row)
	{
		if (row == 0)
		{
			return new ItemStack(StorageCraftItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED);
		}
		else if (row == 1)
		{
			return new ItemStack(StorageCraftBlocks.GRID, 1, 0);
		}
		else if (row == 2)
		{
			return new ItemStack(StorageCraftItems.PATTERN);
		}

		return null;
	}

	@Override
	public ItemStack getResult()
	{
		return new ItemStack(StorageCraftBlocks.GRID, 1, EnumGridType.PATTERN.getId());
	}

	@Override
	public int getDuration()
	{
		return 500;
	}
}
