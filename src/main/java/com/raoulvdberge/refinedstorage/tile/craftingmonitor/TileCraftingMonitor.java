package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;

import javax.annotation.Nonnull;

public class TileCraftingMonitor extends TileNode<NetworkNodeCraftingMonitor> {
    public static final TileDataParameter<Boolean> VIEW_AUTOMATED = new TileDataParameter<>(DataSerializers.BOOLEAN, true, new ITileDataProducer<Boolean, TileCraftingMonitor>() {
        @Override
        public Boolean getValue(TileCraftingMonitor tile) {
            return tile.getNode().canViewAutomated();
        }
    }, new ITileDataConsumer<Boolean, TileCraftingMonitor>() {
        @Override
        public void setValue(TileCraftingMonitor tile, Boolean value) {
            tile.getNode().setViewAutomated(value);
            tile.getNode().markDirty();

            if (tile.getNode().getNetwork() != null) {
                tile.getNode().getNetwork().sendCraftingMonitorUpdate();
            }
        }
    });

    public TileCraftingMonitor() {
        dataManager.addWatchedParameter(VIEW_AUTOMATED);
    }

    @Override
    @Nonnull
    public NetworkNodeCraftingMonitor createNode() {
        return new NetworkNodeCraftingMonitor(this);
    }
}
