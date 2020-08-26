package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.CableNetworkNode
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class CableTile : NetworkNodeTile<CableNetworkNode>(RSTiles.CABLE) {
    override fun createNode(world: World, pos: BlockPos): CableNetworkNode {
        return CableNetworkNode(world, pos)
    }
}