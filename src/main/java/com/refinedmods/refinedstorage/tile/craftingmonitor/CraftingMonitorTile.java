package com.refinedmods.refinedstorage.tile.craftingmonitor;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.CraftingMonitorNetworkNode;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class CraftingMonitorTile extends NetworkNodeTile<CraftingMonitorNetworkNode> {
    public static final TileDataParameter<Optional<UUID>, CraftingMonitorTile> TAB_SELECTED = new TileDataParameter<>(EntityDataSerializers.OPTIONAL_UUID, Optional.empty(), t -> t.getNode().getTabSelected(), (t, v) -> {
        if (v.isPresent() && t.getNode().getTabSelected().isPresent() && v.get().equals(t.getNode().getTabSelected().get())) {
            t.getNode().setTabSelected(Optional.empty());
        } else {
            t.getNode().setTabSelected(v);
        }

        t.getNode().markDirty();
    });
    public static final TileDataParameter<Integer, CraftingMonitorTile> TAB_PAGE = new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getTabPage(), (t, v) -> {
        if (v >= 0) {
            t.getNode().setTabPage(v);
            t.getNode().markDirty();
        }
    });

    public CraftingMonitorTile(BlockPos pos, BlockState state) {
        super(RSTiles.CRAFTING_MONITOR, pos, state);

        dataManager.addWatchedParameter(TAB_SELECTED);
        dataManager.addWatchedParameter(TAB_PAGE);
    }

    @Override
    @Nonnull
    public CraftingMonitorNetworkNode createNode(Level world, BlockPos pos) {
        return new CraftingMonitorNetworkNode(world, pos);
    }
}
