package storagecraft.storage;

import net.minecraft.item.ItemStack;
import storagecraft.item.ItemStorageCell;

public class CellStorage extends NBTStorage
{
	public CellStorage(ItemStack cell, int priority)
	{
		super(cell.getTagCompound(), getCapacity(cell), priority);
	}

	public static int getCapacity(ItemStack cell)
	{
		switch (cell.getItemDamage())
		{
			case ItemStorageCell.TYPE_1K:
				return 1000;
			case ItemStorageCell.TYPE_4K:
				return 4000;
			case ItemStorageCell.TYPE_16K:
				return 16000;
			case ItemStorageCell.TYPE_64K:
				return 64000;
			case ItemStorageCell.TYPE_CREATIVE:
				return -1;
		}

		return 0;
	}
}
