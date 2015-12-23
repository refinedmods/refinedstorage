package storagecraft.tile.solderer;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import storagecraft.StorageCraftBlocks;
import storagecraft.StorageCraftItems;
import storagecraft.item.ItemProcessor;

public class SoldererRecipeDrive implements ISoldererRecipe
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
			return new ItemStack(StorageCraftBlocks.MACHINE_CASING);
		}
		else if (row == 2)
		{
			return new ItemStack(Blocks.chest);
		}

		return null;
	}

	@Override
	public ItemStack getResult()
	{
		return new ItemStack(StorageCraftBlocks.DRIVE);
	}

	@Override
	public int getDuration()
	{
		return 400;
	}
}
