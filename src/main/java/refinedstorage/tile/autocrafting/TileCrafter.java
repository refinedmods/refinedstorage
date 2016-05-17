package refinedstorage.tile.autocrafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerCrafter;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.TileMachine;
import refinedstorage.tile.autocrafting.task.ICraftingTask;

public class TileCrafter extends TileMachine implements IInventory {
    private InventorySimple inventory = new InventorySimple("crafter", PATTERN_SLOTS + 4, this);

    public static final int PATTERN_SLOTS = 6;

    @Override
    public int getEnergyUsage() {
        return 2 + RefinedStorageUtils.getUpgradeEnergyUsage(inventory, PATTERN_SLOTS);
    }

    @Override
    public void updateMachine() {
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerCrafter.class;
    }

    @Override
    public void onDisconnected(World world) {
        for (ICraftingTask task : controller.getCraftingTasks()) {
            if (task.getPattern().getCrafter(worldObj) == this) {
                controller.cancelCraftingTask(task);
            }
        }

        super.onDisconnected(world);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        RefinedStorageUtils.restoreInventory(inventory, 0, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        RefinedStorageUtils.saveInventory(inventory, 0, nbt);
    }

    public int getSpeed() {
        return 20 - (RefinedStorageUtils.getUpgradeCount(inventory, ItemUpgrade.TYPE_SPEED, PATTERN_SLOTS) * 4);
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
    public boolean hasCustomName() {
        return inventory.hasCustomName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return inventory.getDisplayName();
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public IInventory getDroppedInventory() {
        return inventory;
    }
}
