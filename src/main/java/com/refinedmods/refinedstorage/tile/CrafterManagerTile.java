package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.CrafterManagerScreen;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrafterManagerTile extends NetworkNodeTile<CrafterManagerNetworkNode> {
    public static final TileDataParameter<Integer, CrafterManagerTile> SIZE = new TileDataParameter<>(DataSerializers.INT, IGrid.SIZE_STRETCH, t -> t.getNode().getSize(), (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.getNode().setSize(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(CrafterManagerScreen.class, BaseScreen::init));
    public static final TileDataParameter<Integer, CrafterManagerTile> SEARCH_BOX_MODE = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNode().getSearchBoxMode(), (t, v) -> {
        if (IGrid.isValidSearchBoxMode(v)) {
            t.getNode().setSearchBoxMode(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(CrafterManagerScreen.class, crafterManager -> crafterManager.getSearchField().setMode(p)));

    public CrafterManagerTile() {
        super(RSTiles.CRAFTER_MANAGER);

        dataManager.addWatchedParameter(SIZE);
        dataManager.addWatchedParameter(SEARCH_BOX_MODE);
    }

    @Override
    public CrafterManagerNetworkNode createNode(World world, BlockPos pos) {
        return new CrafterManagerNetworkNode(world, pos);
    }
}
