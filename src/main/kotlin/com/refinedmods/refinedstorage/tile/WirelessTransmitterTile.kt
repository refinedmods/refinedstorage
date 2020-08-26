package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.WirelessTransmitterNetworkNode
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Function

class WirelessTransmitterTile : NetworkNodeTile<WirelessTransmitterNetworkNode?>(RSTiles.WIRELESS_TRANSMITTER) {
    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): WirelessTransmitterNetworkNode {
        return WirelessTransmitterNetworkNode(world, pos)
    }

    companion object {
        val RANGE = TileDataParameter(DataSerializers.VARINT, 0, Function { t: WirelessTransmitterTile -> t.getNode()!!.range })
    }

    init {
        dataManager.addWatchedParameter(RANGE)
    }
}