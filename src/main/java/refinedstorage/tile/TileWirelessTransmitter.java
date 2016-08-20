package refinedstorage.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RefinedStorage;
import refinedstorage.api.network.IWirelessTransmitter;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerUpgrade;
import refinedstorage.item.ItemUpgrade;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

public class TileWirelessTransmitter extends TileNode implements IWirelessTransmitter {
    public static final TileDataParameter<Integer> RANGE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileWirelessTransmitter>() {
        @Override
        public Integer getValue(TileWirelessTransmitter tile) {
            return tile.getRange();
        }
    });

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_RANGE);

    public TileWirelessTransmitter() {
        dataManager.addWatchedParameter(RANGE);
    }

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.wirelessTransmitterUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(upgrades, 0, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        writeItems(upgrades, 0, tag);

        return tag;
    }

    @Override
    public int getRange() {
        return RefinedStorage.INSTANCE.wirelessTransmitterBaseRange + (upgrades.getUpgradeCount(ItemUpgrade.TYPE_RANGE) * RefinedStorage.INSTANCE.wirelessTransmitterRangePerUpgrade);
    }

    @Override
    public BlockPos getOrigin() {
        return pos;
    }

    public ItemHandlerBasic getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return upgrades;
    }

    @Override
    public boolean canConduct(EnumFacing direction) {
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) upgrades;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
