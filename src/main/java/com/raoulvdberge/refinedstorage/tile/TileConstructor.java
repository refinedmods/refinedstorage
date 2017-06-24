package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeConstructor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileConstructor extends TileNode<NetworkNodeConstructor> {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean> DROP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileConstructor>() {
        @Override
        public Boolean getValue(TileConstructor tile) {
            return tile.getNode().isDrop();
        }
    }, new ITileDataConsumer<Boolean, TileConstructor>() {
        @Override
        public void setValue(TileConstructor tile, Boolean value) {
            tile.getNode().setDrop(value);
            tile.getNode().markDirty();
        }
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
