package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCrafterManager;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.GuiCrafterManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileCrafterManager extends TileNode<NetworkNodeCrafterManager> {
    public static final TileDataParameter<Integer, TileCrafterManager> SIZE = new TileDataParameter<>(DataSerializers.VARINT, IGrid.SIZE_STRETCH, t -> t.getNode().getSize(), (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.getNode().setSize(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> GuiBase.executeLater(GuiCrafterManager.class, GuiBase::initGui));

    public TileCrafterManager() {
        dataManager.addWatchedParameter(SIZE);
    }

    @Override
    public NetworkNodeCrafterManager createNode(World world, BlockPos pos) {
        return new NetworkNodeCrafterManager(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeCrafterManager.ID;
    }
}
