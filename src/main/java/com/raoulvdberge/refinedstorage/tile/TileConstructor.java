package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeConstructor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileConstructor extends TileNode<NetworkNodeConstructor> {
    public static final TileDataParameter<Integer, TileConstructor> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileConstructor> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean, TileConstructor> DROP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().isDrop(), (t, v) -> {
        t.getNode().setDrop(v);
        t.getNode().markDirty();
    });

    public TileConstructor() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(DROP);
    }

    @Override
    @Nonnull
    public NetworkNodeConstructor createNode(World world, BlockPos pos) {
        return new NetworkNodeConstructor(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeConstructor.ID;
    }
}
