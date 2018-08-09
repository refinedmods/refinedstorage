package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.google.common.base.Optional;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TileCraftingMonitor extends TileNode<NetworkNodeCraftingMonitor> {
    public static final TileDataParameter<Optional<UUID>, TileCraftingMonitor> TAB_SELECTED = new TileDataParameter<>(DataSerializers.OPTIONAL_UNIQUE_ID, Optional.absent(), t -> t.getNode().getTabSelected(), (t, v) -> {
        if (v.isPresent() && t.getNode().getTabSelected().isPresent() && v.get().equals(t.getNode().getTabSelected().get())) {
            t.getNode().setTabSelected(Optional.absent());
        } else {
            t.getNode().setTabSelected(v);
        }

        t.getNode().markDirty();
    });
    public static final TileDataParameter<Integer, TileCraftingMonitor> TAB_PAGE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getTabPage(), (t, v) -> {
        if (v >= 0) {
            t.getNode().setTabPage(v);
            t.getNode().markDirty();
        }
    });

    public TileCraftingMonitor() {
        dataManager.addWatchedParameter(TAB_SELECTED);
        dataManager.addWatchedParameter(TAB_PAGE);
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
