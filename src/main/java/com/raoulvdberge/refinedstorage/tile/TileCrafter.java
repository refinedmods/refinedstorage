package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCrafter;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;

import javax.annotation.Nonnull;

public class TileCrafter extends TileNode<NetworkNodeCrafter> {
    public static final TileDataParameter<Boolean> TRIGGERED_AUTOCRAFTING = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileCrafter>() {
        @Override
        public Boolean getValue(TileCrafter tile) {
            return tile.getNode().isTriggeredAutocrafting();
        }
    }, new ITileDataConsumer<Boolean, TileCrafter>() {
        @Override
        public void setValue(TileCrafter tile, Boolean value) {
            tile.getNode().setTriggeredAutocrafting(value);
            tile.getNode().markDirty();
        }
    });

    public TileCrafter() {
        dataManager.addWatchedParameter(TRIGGERED_AUTOCRAFTING);
    }

    @Override
    @Nonnull
    public NetworkNodeCrafter createNode() {
        return new NetworkNodeCrafter(this);
    }
}
