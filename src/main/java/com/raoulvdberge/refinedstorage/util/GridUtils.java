package com.raoulvdberge.refinedstorage.util;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.raoulvdberge.refinedstorage.tile.grid.GridTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

public class GridUtils {
    public static ResourceLocation getNetworkNodeId(GridType type) {
        switch (type) {
            case NORMAL:
                return GridNetworkNode.ID;
            case CRAFTING:
                return GridNetworkNode.CRAFTING_ID;
            case PATTERN:
                return GridNetworkNode.PATTERN_ID;
            case FLUID:
                return GridNetworkNode.FLUID_ID;
            default:
                throw new IllegalArgumentException("Unknown grid type " + type);
        }
    }

    public static TileEntityType<GridTile> getTileEntityType(GridType type) {
        switch (type) {
            case NORMAL:
                return RSTiles.GRID;
            case CRAFTING:
                return RSTiles.CRAFTING_GRID;
            case PATTERN:
                return RSTiles.PATTERN_GRID;
            case FLUID:
                return RSTiles.FLUID_GRID;
            default:
                throw new IllegalArgumentException("Unknown grid type " + type);
        }
    }
}
