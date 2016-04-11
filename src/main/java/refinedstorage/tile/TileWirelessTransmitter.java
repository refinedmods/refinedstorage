package refinedstorage.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.util.InventoryUtils;

public class TileWirelessTransmitter extends TileMachine implements IInventory {
    public static final int RANGE_PER_UPGRADE = 8;
    public static final int RF_PER_UPGRADE = 8;

    private InventorySimple inventory = new InventorySimple("upgrades", 9, this);

    @Override
    public int getEnergyUsage() {
        return 8 + (getUpgrades() * RF_PER_UPGRADE);
    }

    @Override
    public void updateMachine() {
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        InventoryUtils.restoreInventory(inventory, 0, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        InventoryUtils.saveInventory(inventory, 0, nbt);
    }

    public int getRange() {
        return 16 + (getUpgrades() * RANGE_PER_UPGRADE);
    }

    private int getUpgrades() {
        int upgrades = 0;

        for (int i = 0; i < 9; ++i) {
            if (inventory.getStackInSlot(i) != null) {
                upgrades++;
            }
        }

        return upgrades;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return null;
    }

    @Override
    public IInventory getDroppedInventory() {
        return inventory;
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
    public ItemStack decrStackSize(int slot, int count) {
        return inventory.decrStackSize(slot, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        return inventory.removeStackFromSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
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
    public void openInventory(EntityPlayer player) {
        inventory.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        inventory.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return inventory.isItemValidForSlot(slot, stack);
    }

    @Override
    public int getField(int id) {
        return inventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inventory.getFieldCount();
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inventory.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return inventory.getDisplayName();
    }
}
