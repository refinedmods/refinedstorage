package storagecraft.tile;

import java.util.List;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import storagecraft.storage.IStorage;
import storagecraft.storage.IStorageProvider;
import storagecraft.storage.StorageItem;
import storagecraft.util.InventoryUtils;

public class TileStorageProxy extends TileMachine implements IStorageProvider, IStorage {
	public IInventory getInventory() {
		TileEntity tile = worldObj.getTileEntity(xCoord + getDirection().offsetX, yCoord + getDirection().offsetY, zCoord + getDirection().offsetZ);

		if (tile instanceof IInventory) {
			return (IInventory) tile;
		}

		return null;
	}

	@Override
	public int getEnergyUsage() {
		return 2;
	}

	@Override
	public void updateMachine() {
	}

	@Override
	public void addItems(List<StorageItem> items) {
		IInventory inventory = getInventory();

		if (inventory != null) {
			for (int i = 0; i < inventory.getSizeInventory(); ++i) {
				if (inventory.getStackInSlot(i) != null) {
					items.add(new StorageItem(inventory.getStackInSlot(i)));
				}
			}
		}
	}

	@Override
	public void push(ItemStack stack) {
		IInventory inventory = getInventory();

		if (inventory == null) {
			return;
		}

		int toGo = stack.stackSize;

		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			ItemStack slot = inventory.getStackInSlot(i);

			if (slot == null) {
				inventory.setInventorySlotContents(i, stack);

				return;
			} else if (InventoryUtils.compareStackNoQuantity(slot, stack)) {
				int toAdd = toGo;

				if (slot.stackSize + toAdd > slot.getMaxStackSize()) {
					toAdd = slot.getMaxStackSize() - slot.stackSize;
				}

				slot.stackSize += toAdd;

				toGo -= toAdd;

				if (toGo == 0) {
					return;
				}
			}
		}
	}

	@Override
	public int take(ItemStack stack) {
		IInventory inventory = getInventory();

		if (inventory == null) {
			return 0;
		}

		int quantity = stack.stackSize;

		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			ItemStack slot = inventory.getStackInSlot(i);

			if (slot != null && InventoryUtils.compareStackNoQuantity(slot, stack)) {
				if (quantity > slot.stackSize) {
					quantity = slot.stackSize;
				}

				slot.stackSize -= quantity;

				if (slot.stackSize == 0) {
					inventory.setInventorySlotContents(i, null);
				}

				return quantity;
			}
		}

		return 0;
	}

	@Override
	public boolean canPush(ItemStack stack) {
		IInventory inventory = getInventory();

		if (inventory == null) {
			return false;
		}

		int toGo = stack.stackSize;

		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			ItemStack slot = inventory.getStackInSlot(i);

			if (slot == null) {
				return true;
			} else if (InventoryUtils.compareStackNoQuantity(slot, stack)) {
				int toAdd = toGo;

				if (slot.stackSize + toAdd > slot.getMaxStackSize()) {
					toAdd = slot.getMaxStackSize() - slot.stackSize;
				}

				toGo -= toAdd;

				if (toGo == 0) {
					break;
				}
			}
		}

		return toGo == 0;
	}

	@Override
	public void addStorages(List<IStorage> storages) {
		storages.add(this);
	}
}
