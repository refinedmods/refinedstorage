package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.WirelessTransmitterNetworkNode;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class WirelessTransmitterTile extends NetworkNodeTile<WirelessTransmitterNetworkNode> {
    public static final TileDataParameter<Integer, WirelessTransmitterTile> RANGE = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNode().getRange());

    public WirelessTransmitterTile() {
        super(RSTiles.WIRELESS_TRANSMITTER);

        dataManager.addWatchedParameter(RANGE);
    }

    @Override
    @Nonnull
    public WirelessTransmitterNetworkNode createNode(World world, BlockPos pos) {
        return new WirelessTransmitterNetworkNode(world, pos);
    }
}
