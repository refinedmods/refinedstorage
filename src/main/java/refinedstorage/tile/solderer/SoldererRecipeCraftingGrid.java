package refinedstorage.tile.solderer;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageItems;
import refinedstorage.block.EnumGridType;
import refinedstorage.item.ItemProcessor;

public class SoldererRecipeCraftingGrid implements ISoldererRecipe
{
	@Override
	public ItemStack getRow(int row)
	{
		if (row == 0)
		{
			return new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED);
		}
		else if (row == 1)
		{
			return new ItemStack(RefinedStorageBlocks.GRID, 1, 0);
		}
		else if (row == 2)
		{
			return new ItemStack(Blocks.crafting_table);
		}

		return null;
	}

	@Override
	public ItemStack getResult()
	{
		return new ItemStack(RefinedStorageBlocks.GRID, 1, EnumGridType.CRAFTING.getId());
	}

	@Override
	public int getDuration()
	{
		return 500;
	}
}
