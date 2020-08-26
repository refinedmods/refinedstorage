package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkTransmitterNetworkNode
import com.refinedmods.refinedstorage.tile.data.RSSerializers
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.util.Direction
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import java.util.*
import java.util.function.Function

class NetworkTransmitterTile : NetworkNodeTile<NetworkTransmitterNetworkNode?>(RSTiles.NETWORK_TRANSMITTER) {
    private val networkCardCapability: LazyOptional<IItemHandler> = LazyOptional.of({ getNode()!!.getNetworkCard() })
    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): NetworkTransmitterNetworkNode {
        return NetworkTransmitterNetworkNode(world, pos)
    }

    @Nonnull
    override fun <T> getCapability(@Nonnull cap: Capability<T>, @Nullable direction: Direction?): LazyOptional<T> {
        return if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            networkCardCapability.cast()
        } else super.getCapability<T>(cap, direction)
    }

    companion object {
        val DISTANCE = TileDataParameter(DataSerializers.VARINT, 0, Function { t: NetworkTransmitterTile -> t.getNode()!!.distance })
        val RECEIVER_DIMENSION: TileDataParameter<Optional<Identifier>, NetworkTransmitterTile> = TileDataParameter<T, E>(RSSerializers.OPTIONAL_RESOURCE_LOCATION_SERIALIZER, Optional.empty<Any>(), label@ Function<E, T> { t: E ->
            if (t.getNode().getReceiverDimension() != null) {
                return@label Optional.of(t.getNode().getReceiverDimension().func_240901_a_())
            }
            Optional.empty<Any?>()
        })
    }

    init {
        dataManager.addWatchedParameter(DISTANCE)
        dataManager.addWatchedParameter(RECEIVER_DIMENSION)
    }
}