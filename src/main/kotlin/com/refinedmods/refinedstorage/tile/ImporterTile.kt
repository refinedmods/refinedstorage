package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.ImporterNetworkNode
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ImporterTile : NetworkNodeTile<ImporterNetworkNode?>(RSTiles.IMPORTER) {
    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): ImporterNetworkNode {
        return ImporterNetworkNode(world, pos)
    }

    companion object {
        val COMPARE: TileDataParameter<Int, ImporterTile> = IComparable.Companion.createParameter()
        val WHITELIST_BLACKLIST: TileDataParameter<Int, ImporterTile> = IWhitelistBlacklist.Companion.createParameter()
        val TYPE: TileDataParameter<Int, ImporterTile> = IType.Companion.createParameter()
    }

    init {
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST)
        dataManager.addWatchedParameter(TYPE)
    }
}