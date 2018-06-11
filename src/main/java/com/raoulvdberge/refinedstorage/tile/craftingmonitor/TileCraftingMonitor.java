package com.raoulvdberge.refinedstorage.tile.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCraftingMonitor;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.GuiCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileCraftingMonitor extends TileNode<NetworkNodeCraftingMonitor> {
    public static final TileDataParameter<Integer, TileCraftingMonitor> SIZE = new TileDataParameter<>(DataSerializers.VARINT, IGrid.SIZE_STRETCH, t -> t.getNode().getSize(), (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.getNode().setSize(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> GuiBase.executeLater(GuiCraftingMonitor.class, GuiBase::initGui));

    public TileCraftingMonitor() {
        dataManager.addWatchedParameter(SIZE);
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
