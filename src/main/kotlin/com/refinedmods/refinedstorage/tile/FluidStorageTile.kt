package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType
import com.refinedmods.refinedstorage.tile.config.IAccessType
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IPrioritizable
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.tile.data.RSSerializers
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.util.FluidStorageBlockUtils
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Function

class FluidStorageTile(type: FluidStorageType) : NetworkNodeTile<FluidStorageNetworkNode?>(FluidStorageBlockUtils.getBlockEntityType(type)) {
    private val type: FluidStorageType
    val fluidStorageType: FluidStorageType
        get() = type

    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): FluidStorageNetworkNode {
        return FluidStorageNetworkNode(world, pos, type)
    }

    companion object {
        val PRIORITY: TileDataParameter<Int, FluidStorageTile> = IPrioritizable.Companion.createParameter()
        val COMPARE: TileDataParameter<Int, FluidStorageTile> = IComparable.Companion.createParameter()
        val WHITELIST_BLACKLIST: TileDataParameter<Int, FluidStorageTile> = IWhitelistBlacklist.Companion.createParameter()
        val ACCESS_TYPE: TileDataParameter<AccessType?, FluidStorageTile> = IAccessType.Companion.createParameter()
        val STORED: TileDataParameter<Long, FluidStorageTile> = TileDataParameter<T, E>(RSSerializers.LONG_SERIALIZER, 0L, Function<E, T> { t: E -> if (t.getNode().getStorage() != null) t.getNode().getStorage().getStored() else 0 })
    }

    init {
        this.type = type
        dataManager.addWatchedParameter(PRIORITY)
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST)
        dataManager.addWatchedParameter(STORED)
        dataManager.addWatchedParameter(ACCESS_TYPE)
    }
}