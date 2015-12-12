package storagecraft.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import storagecraft.SC;
import storagecraft.inventory.InventorySC;

public class TileDrive extends TileMachine implements IInventory {
	private InventorySC inventory = new InventorySC("drive", 8);
	
	@Override
	public int getEnergyUsage() {
		return 5;
	}
	
	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return inventory.decrStackSize(slot, amount);
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}
	
	@Override
	public String getInventoryName() {
		return inventory.getInventoryName();
	}
	
	@Override
	public boolean hasCustomInventoryName() {
		return inventory.hasCustomInventoryName();
	}
	
	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}
	
	@Override
	public void openInventory() {
		inventory.openInventory();
	}
	
	@Override
	public void closeInventory() {
		inventory.closeInventory();
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		SC.restoreInventory(this, nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		SC.saveInventory(this, nbt);
	}
}
