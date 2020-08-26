package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.RelayNetworkNode
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class RelayTile : NetworkNodeTile<RelayNetworkNode?>(RSTiles.RELAY) {
    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): RelayNetworkNode {
        return RelayNetworkNode(world, pos)
    }
}