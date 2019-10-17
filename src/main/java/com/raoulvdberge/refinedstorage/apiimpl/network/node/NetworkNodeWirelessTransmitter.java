package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.IWirelessTransmitter;
import com.raoulvdberge.refinedstorage.inventory.item.BaseItemHandler;
import com.raoulvdberge.refinedstorage.inventory.item.UpgradeItemHandler;
import com.raoulvdberge.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.raoulvdberge.refinedstorage.item.UpgradeItem;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class NetworkNodeWirelessTransmitter extends NetworkNode implements IWirelessTransmitter {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "wireless_transmitter");

    private UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4 /* TODO , ItemUpgrade.TYPE_RANGE*/).addListener(new NetworkNodeInventoryListener(this));

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
    public ResourceLocation getId() {
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
        return RS.INSTANCE.config.wirelessTransmitterBaseRange + (upgrades.getUpgradeCount(UpgradeItem.Type.RANGE) * RS.INSTANCE.config.wirelessTransmitterRangePerUpgrade);
    }

    @Override
    public BlockPos getOrigin() {
        return pos;
    }

    @Override
    public int getDimension() {
        return world.getDimension().getType().getId();
    }

    public BaseItemHandler getUpgrades() {
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
    public void visit(Operator operator) {
        operator.apply(world, pos.offset(Direction.DOWN), Direction.UP);
    }
}
