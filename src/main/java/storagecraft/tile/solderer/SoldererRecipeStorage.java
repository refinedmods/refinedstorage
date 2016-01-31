package storagecraft.tile.solderer;

import net.minecraft.item.ItemStack;
import storagecraft.StorageCraftBlocks;
import storagecraft.StorageCraftItems;
import storagecraft.block.EnumStorageType;
import storagecraft.item.ItemBlockStorage;
import storagecraft.item.ItemProcessor;

public class SoldererRecipeStorage implements ISoldererRecipe
{
	private EnumStorageType type;
	private int storagePart;

	public SoldererRecipeStorage(EnumStorageType type, int storagePart)
	{
		this.type = type;
		this.storagePart = storagePart;
	}

	@Override
	public ItemStack getRow(int row)
	{
		if (row == 0)
		{
			return new ItemStack(StorageCraftItems.STORAGE_PART, 1, storagePart);
		}
		else if (row == 1)
		{
			return new ItemStack(StorageCraftBlocks.MACHINE_CASING);
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
		return ItemBlockStorage.initNBT(new ItemStack(StorageCraftBlocks.STORAGE, 1, type.getId()));
	}

	@Override
	public int getDuration()
	{
		return 200;
	}
}
