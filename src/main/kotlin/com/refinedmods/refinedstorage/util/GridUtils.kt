package com.refinedmods.refinedstorage.util

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode
import com.refinedmods.refinedstorage.tile.grid.GridTile
import net.minecraft.tileentity.BlockEntityType
import net.minecraft.util.Identifier

object GridUtils {
    fun getNetworkNodeId(type: GridType): Identifier {
        return when (type) {
            GridType.NORMAL -> GridNetworkNode.ID
            GridType.CRAFTING -> GridNetworkNode.CRAFTING_ID
            GridType.PATTERN -> GridNetworkNode.PATTERN_ID
            GridType.FLUID -> GridNetworkNode.FLUID_ID
            else -> throw IllegalArgumentException("Unknown grid type $type")
        }
    }

    fun getBlockEntityType(type: GridType): BlockEntityType<GridTile> {
        return when (type) {
            GridType.NORMAL -> RSTiles.GRID
            GridType.CRAFTING -> RSTiles.CRAFTING_GRID
            GridType.PATTERN -> RSTiles.PATTERN_GRID
            GridType.FLUID -> RSTiles.FLUID_GRID
            else -> throw IllegalArgumentException("Unknown grid type $type")
        }
    }
}