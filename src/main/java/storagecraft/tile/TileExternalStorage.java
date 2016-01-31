package storagecraft.tile;

import java.util.List;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import storagecraft.storage.IStorage;
import storagecraft.storage.IStorageProvider;
import storagecraft.storage.StorageItem;
import storagecraft.util.InventoryUtils;

public class TileExternalStorage extends TileMachine implements IStorageProvider, IStorage
{
	public IInventory getInventory()
	{
		TileEntity tile = worldObj.getTileEntity(pos.offset(getDirection()));

		if (tile instanceof IInventory)
		{
			return (IInventory) tile;
		}

		return null;
	}

	@Override
	public int getEnergyUsage()
	{
		return 2;
	}

	@Override
	public void updateMachine()
	{
	}

	@Override
	public void addItems(List<StorageItem> items)
	{
		IInventory inventory = getInventory();

		if (inventory != null)
		{
			for (int i = 0; i < inventory.getSizeInventory(); ++i)
			{
				if (inventory.getStackInSlot(i) != null)
				{
					items.add(new StorageItem(inventory.getStackInSlot(i)));
				}
			}
		}
	}

	@Override
	public void push(ItemStack stack)
	{
		IInventory inventory = getInventory();

		if (inventory == null)
		{
			return;
		}

		InventoryUtils.pushToInventory(inventory, stack);
	}

	@Override
	public ItemStack take(ItemStack stack, int flags)
	{
		IInventory inventory = getInventory();

		if (inventory == null)
		{
			return null;
		}

		int quantity = stack.stackSize;

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack slot = inventory.getStackInSlot(i);

			if (slot != null && InventoryUtils.compareStack(slot, stack, flags))
			{
				if (quantity > slot.stackSize)
				{
					quantity = slot.stackSize;
				}

				slot.stackSize -= quantity;

				if (slot.stackSize == 0)
				{
					inventory.setInventorySlotContents(i, null);
				}

				ItemStack newItem = slot.copy();

				newItem.stackSize = quantity;

				return newItem;
			}
		}

		return null;
	}

	@Override
	public boolean canPush(ItemStack stack)
	{
		IInventory inventory = getInventory();

		if (inventory == null)
		{
			return false;
		}

		return InventoryUtils.canPushToInventory(inventory, stack);
	}

	@Override
	public int getPriority()
	{
		// @TODO: Priority on this stuff
		return 0;
	}

	@Override
	public void addStorages(List<IStorage> storages)
	{
		storages.add(this);
	}
}
