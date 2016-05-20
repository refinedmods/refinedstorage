package refinedstorage.tile;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerWirelessTransmitter;
import refinedstorage.inventory.InventorySimple;
import refinedstorage.item.ItemUpgrade;

public class TileWirelessTransmitter extends TileMachine {
    public static final int RANGE_PER_UPGRADE = 8;

    private InventorySimple inventory = new InventorySimple("upgrades", 4, this);

    @Override
    public int getEnergyUsage() {
        return 8 + RefinedStorageUtils.getUpgradeEnergyUsage(inventory);
    }

    @Override
    public void updateMachine() {
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

    public int getRange() {
        return 16 + (RefinedStorageUtils.getUpgradeCount(inventory, ItemUpgrade.TYPE_RANGE) * RANGE_PER_UPGRADE);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerWirelessTransmitter.class;
    }

    @Override
    public IInventory getDroppedInventory() {
        return inventory;
    }
}
