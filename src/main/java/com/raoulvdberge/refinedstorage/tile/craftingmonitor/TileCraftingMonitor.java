package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.NetworkNodeTile;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class TileCraftingMonitor extends NetworkNodeTile<NetworkNodeCraftingMonitor> {
    public static final TileDataParameter<Optional<UUID>, TileCraftingMonitor> TAB_SELECTED = new TileDataParameter<>(DataSerializers.OPTIONAL_UNIQUE_ID, Optional.empty(), t -> t.getNode().getTabSelected(), (t, v) -> {
        if (v.isPresent() && t.getNode().getTabSelected().isPresent() && v.get().equals(t.getNode().getTabSelected().get())) {
            t.getNode().setTabSelected(Optional.empty());
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
        super(RSTiles.CRAFTING_MONITOR);

        dataManager.addWatchedParameter(TAB_SELECTED);
        dataManager.addWatchedParameter(TAB_PAGE);
    }

    @Override
    @Nonnull
    public NetworkNodeCraftingMonitor createNode(World world, BlockPos pos) {
        return new NetworkNodeCraftingMonitor(world, pos);
    }
}
