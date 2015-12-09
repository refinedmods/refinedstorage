package storagecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryBasic implements IInventory {
	private ItemStack[] inventory;
	private int inventorySize;
	private String name;

	public InventoryBasic(String name, int size) {
		this.name = name;
		this.inventorySize = size;
		this.inventory = new ItemStack[inventorySize];
	}

	public void setInventoryStack(ItemStack[] stack) {
		this.inventory = stack;
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slotIndex) {
		return inventory[slotIndex];
	}

	@Override
	public ItemStack decrStackSize(int slotIndex, int decrementAmount) {
		ItemStack itemStack = getStackInSlot(slotIndex);
		if (itemStack != null) {
			if (itemStack.stackSize <= decrementAmount) {
				setInventorySlotContents(slotIndex, null);
			} else {
				itemStack = itemStack.splitStack(decrementAmount);

				if (itemStack.stackSize == 0) {
					setInventorySlotContents(slotIndex, null);
				}
			}
		}
		return itemStack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slotIndex) {
		ItemStack itemStack = getStackInSlot(slotIndex);
		if (itemStack != null) {
			setInventorySlotContents(slotIndex, null);
		}
		return itemStack;
	}

	@Override
	public void setInventorySlotContents(int slotIndex, ItemStack itemStack) {
		if (itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {
			itemStack.stackSize = getInventoryStackLimit();
		}

		inventory[slotIndex] = itemStack;
	}

	@Override
	public String getInventoryName() {
		return this.name;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		return true;
	}

	@Override
	public void markDirty() {
	}
}
