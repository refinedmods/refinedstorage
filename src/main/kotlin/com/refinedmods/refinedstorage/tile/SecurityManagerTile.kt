package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.SecurityManagerNetworkNode
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class SecurityManagerTile : NetworkNodeTile<SecurityManagerNetworkNode?>(RSTiles.SECURITY_MANAGER) {
    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): SecurityManagerNetworkNode {
        return SecurityManagerNetworkNode(world, pos)
    }
}