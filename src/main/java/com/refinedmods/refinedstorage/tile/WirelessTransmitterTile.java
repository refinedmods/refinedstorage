package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.WirelessTransmitterNetworkNode;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class WirelessTransmitterTile extends NetworkNodeTile<WirelessTransmitterNetworkNode> {
    public static final TileDataParameter<Integer, WirelessTransmitterTile> RANGE = new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getRange());

    public WirelessTransmitterTile(BlockPos pos, BlockState state) {
        super(RSTiles.WIRELESS_TRANSMITTER, pos, state);

        dataManager.addWatchedParameter(RANGE);
    }

    @Override
    @Nonnull
    public WirelessTransmitterNetworkNode createNode(Level world, BlockPos pos) {
        return new WirelessTransmitterNetworkNode(world, pos);
    }
}
