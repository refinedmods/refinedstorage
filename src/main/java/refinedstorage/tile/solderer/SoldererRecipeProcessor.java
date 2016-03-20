package refinedstorage.tile.solderer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;
import refinedstorage.item.ItemProcessor;

public class SoldererRecipeProcessor implements ISoldererRecipe
{
	private int type;

	public SoldererRecipeProcessor(int type)
	{
		this.type = type;
	}

	@Override
	public ItemStack getRow(int row)
	{
		if (row == 0)
		{
			switch (type)
			{
				case ItemProcessor.TYPE_BASIC:
					return new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_BASIC);
				case ItemProcessor.TYPE_IMPROVED:
					return new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_IMPROVED);
				case ItemProcessor.TYPE_ADVANCED:
					return new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_ADVANCED);
			}
		}
		else if (row == 1)
		{
			return new ItemStack(Items.redstone);
		}
		else if (row == 2)
		{
			return new ItemStack(RefinedStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_PRINTED_SILICON);
		}

		return null;
	}

	@Override
	public ItemStack getResult()
	{
		return new ItemStack(RefinedStorageItems.PROCESSOR, 1, type);
	}

	@Override
	public int getDuration()
	{
		switch (type)
		{
			case ItemProcessor.TYPE_BASIC:
				return 250;
			case ItemProcessor.TYPE_IMPROVED:
				return 300;
			case ItemProcessor.TYPE_ADVANCED:
				return 350;
		}

		return 0;
	}
}
