package storagecraft.tile.solderer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import storagecraft.StorageCraftItems;
import storagecraft.item.ItemProcessor;

public class SoldererRecipePrintedProcessor implements ISoldererRecipe
{
	private int type;

	public SoldererRecipePrintedProcessor(int type)
	{
		this.type = type;
	}

	@Override
	public ItemStack getRow(int row)
	{
		if (row == 1)
		{
			switch (type)
			{
				case ItemProcessor.TYPE_PRINTED_BASIC:
					return new ItemStack(Items.iron_ingot);
				case ItemProcessor.TYPE_PRINTED_IMPROVED:
					return new ItemStack(Items.gold_ingot);
				case ItemProcessor.TYPE_PRINTED_ADVANCED:
					return new ItemStack(Items.diamond);
				case ItemProcessor.TYPE_PRINTED_SILICON:
					return new ItemStack(StorageCraftItems.SILICON);
			}
		}

		return null;
	}

	@Override
	public ItemStack getResult()
	{
		return new ItemStack(StorageCraftItems.PROCESSOR, 1, type);
	}

	@Override
	public int getDuration()
	{
		return 100;
	}
}
