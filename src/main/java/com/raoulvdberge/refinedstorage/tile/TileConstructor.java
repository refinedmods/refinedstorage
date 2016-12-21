package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeConstructor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;

public class TileConstructor extends TileNode {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean> DROP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileConstructor>() {
        @Override
        public Boolean getValue(TileConstructor tile) {
            return ((NetworkNodeConstructor) tile.getNode()).isDrop();
        }
    }, new ITileDataConsumer<Boolean, TileConstructor>() {
        @Override
        public void setValue(TileConstructor tile, Boolean value) {
            ((NetworkNodeConstructor) tile.getNode()).setDrop(value);

            tile.getNode().markDirty();
        }
    });

    public TileConstructor() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(DROP);
    }

    @Override
    public INetworkNode createNode() {
        return new NetworkNodeConstructor(this);
    }
}
