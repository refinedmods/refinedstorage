package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileCraftingMonitor extends TileNode<NetworkNodeCraftingMonitor> {
    public static final TileDataParameter<Boolean, TileCraftingMonitor> VIEW_AUTOMATED = new TileDataParameter<>(DataSerializers.BOOLEAN, true, t -> t.getNode().canViewAutomated(), (t, v) -> {
        t.getNode().setViewAutomated(v);
        t.getNode().markDirty();

        INetwork network = t.getNode().getNetwork();

        if (network != null) {
            network.sendCraftingMonitorUpdate();
        }
    });

    public TileCraftingMonitor() {
        dataManager.addWatchedParameter(VIEW_AUTOMATED);
    }

    @Override
    @Nonnull
    public NetworkNodeCraftingMonitor createNode(World world, BlockPos pos) {
        return new NetworkNodeCraftingMonitor(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeCraftingMonitor.ID;
    }
}
