package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.IWirelessTransmitter;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor.Operator;

public class WirelessTransmitterNetworkNode extends NetworkNode implements IWirelessTransmitter {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "wireless_transmitter");

    private final UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, UpgradeItem.Type.RANGE).addListener(new NetworkNodeInventoryListener(this));

    public WirelessTransmitterNetworkNode(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getWirelessTransmitter().getUsage() + upgrades.getEnergyUsage();
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
        return RS.SERVER_CONFIG.getWirelessTransmitter().getBaseRange() + (upgrades.getUpgradeCount(UpgradeItem.Type.RANGE) * RS.SERVER_CONFIG.getWirelessTransmitter().getRangePerUpgrade());
    }

    @Override
    public BlockPos getOrigin() {
        return pos;
    }

    @Override
    public RegistryKey<World> getDimension() {
        return world.dimension();
    }

    public BaseItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return getUpgrades();
    }

    @Override
    public boolean canConduct(Direction direction) {
        return getDirection() == direction;
    }

    @Override
    public void visit(Operator operator) {
        operator.apply(world, pos.relative(Direction.DOWN), Direction.UP);
    }
}
