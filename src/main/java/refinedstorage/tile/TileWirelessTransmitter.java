package refinedstorage.tile;

import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerWirelessTransmitter;
import refinedstorage.inventory.SimpleItemHandler;
import refinedstorage.item.ItemUpgrade;

public class TileWirelessTransmitter extends TileMachine {
    public static final int RANGE_PER_UPGRADE = 8;

    private SimpleItemHandler upgrades = new SimpleItemHandler(4, this);

    @Override
    public int getEnergyUsage() {
        return 8 + RefinedStorageUtils.getUpgradeEnergyUsage(upgrades);
    }

    @Override
    public void updateMachine() {
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        RefinedStorageUtils.restoreItems(upgrades, 0, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        RefinedStorageUtils.saveItems(upgrades, 0, nbt);
    }

    public int getRange() {
        return 16 + (RefinedStorageUtils.getUpgradeCount(upgrades, ItemUpgrade.TYPE_RANGE) * RANGE_PER_UPGRADE);
    }

    public SimpleItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerWirelessTransmitter.class;
    }

    @Override
    public IItemHandler getDroppedItems() {
        return upgrades;
    }
}
