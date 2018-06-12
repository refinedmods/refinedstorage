package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeExporter;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileExporter extends TileNode<NetworkNodeExporter> {
    public static final TileDataParameter<Integer, TileExporter> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileExporter> TYPE = IType.createParameter();

    public TileExporter() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    @Nonnull
    public NetworkNodeExporter createNode(World world, BlockPos pos) {
        return new NetworkNodeExporter(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeExporter.ID;
    }
}
