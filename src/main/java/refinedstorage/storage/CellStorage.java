package refinedstorage.storage;

import net.minecraft.item.ItemStack;
import refinedstorage.item.ItemStorageCell;
import refinedstorage.tile.TileDrive;
import refinedstorage.tile.settings.ModeSettingUtils;

public class CellStorage extends NBTStorage
{
	private TileDrive drive;

	public CellStorage(ItemStack cell, TileDrive drive)
	{
		super(cell.getTagCompound(), getCapacity(cell), drive.getPriority());

		this.drive = drive;
	}

	@Override
	public boolean canPush(ItemStack stack)
	{
		if (ModeSettingUtils.doesNotViolateMode(drive.getInventory(), drive.getModeSetting(), drive.getCompare(), stack))
		{
			return super.canPush(stack);
		}

		return false;
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
