package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.IWirelessTransmitter;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class NetworkNodeWirelessTransmitter extends NetworkNode implements IWirelessTransmitter {
    public static final String ID = "wireless_transmitter";

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ListenerNetworkNode(this)/* TODO , ItemUpgrade.TYPE_RANGE*/);

    public NetworkNodeWirelessTransmitter(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.wirelessTransmitterUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        StackUtils.readItems(upgrades, 0, tag);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        StackUtils.writeItems(upgrades, 0, tag);

        return tag;
    }

    @Override
    public int getRange() {
        return RS.INSTANCE.config.wirelessTransmitterBaseRange + (upgrades.getUpgradeCount(ItemUpgrade.TYPE_RANGE) * RS.INSTANCE.config.wirelessTransmitterRangePerUpgrade);
    }

    @Override
    public BlockPos getOrigin() {
        return pos;
    }

    @Override
    public int getDimension() {
        return world.getDimension().getType().getId();
    }

    public ItemHandlerBase getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return upgrades;
    }

    @Override
    public boolean canConduct(@Nullable Direction direction) {
        return direction != null && Direction.DOWN.equals(direction);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public void visit(Operator operator) {
        operator.apply(world, pos.offset(Direction.DOWN), Direction.UP);
    }
}
