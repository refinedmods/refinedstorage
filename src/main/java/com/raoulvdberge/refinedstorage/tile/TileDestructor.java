package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeDestructor;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;

import javax.annotation.Nonnull;

public class TileDestructor extends TileNode<NetworkNodeDestructor> {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean> PICKUP = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileDestructor>() {
        @Override
        public Boolean getValue(TileDestructor tile) {
            return tile.getNode().isPickupItem();
        }
    }, new ITileDataConsumer<Boolean, TileDestructor>() {
        @Override
        public void setValue(TileDestructor tile, Boolean value) {
            tile.getNode().setPickupItem(value);
            tile.getNode().markDirty();
        }
    });

    public TileDestructor() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(PICKUP);
    }

    @Override
    @Nonnull
    public NetworkNodeDestructor createNode() {
        return new NetworkNodeDestructor(this);
    }
}
