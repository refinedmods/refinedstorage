package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.WirelessTransmitterNetworkNode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class WirelessTransmitterBlockEntity extends NetworkNodeBlockEntity<WirelessTransmitterNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, WirelessTransmitterBlockEntity> RANGE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getRange());

    public WirelessTransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.WIRELESS_TRANSMITTER, pos, state);

        dataManager.addWatchedParameter(RANGE);
    }

    @Override
    @Nonnull
    public WirelessTransmitterNetworkNode createNode(Level level, BlockPos pos) {
        return new WirelessTransmitterNetworkNode(level, pos);
    }
}
