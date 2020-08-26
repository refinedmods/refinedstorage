package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkReceiverNetworkNode
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class NetworkReceiverTile : NetworkNodeTile<NetworkReceiverNetworkNode?>(RSTiles.NETWORK_RECEIVER) {
    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): NetworkReceiverNetworkNode {
        return NetworkReceiverNetworkNode(world, pos)
    }
}