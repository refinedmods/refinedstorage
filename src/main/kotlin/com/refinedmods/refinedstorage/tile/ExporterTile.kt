package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.ExporterNetworkNode
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ExporterTile : NetworkNodeTile<ExporterNetworkNode?>(RSTiles.EXPORTER) {
    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): ExporterNetworkNode {
        return ExporterNetworkNode(world, pos)
    }

    companion object {
        val COMPARE: TileDataParameter<Int, ExporterTile> = IComparable.Companion.createParameter()
        val TYPE: TileDataParameter<Int, ExporterTile> = IType.Companion.createParameter()
    }

    init {
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(TYPE)
    }
}