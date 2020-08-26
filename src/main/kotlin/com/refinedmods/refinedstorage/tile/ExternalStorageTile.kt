package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.apiimpl.network.node.ExternalStorageNetworkNode
import com.refinedmods.refinedstorage.tile.config.*
import com.refinedmods.refinedstorage.tile.data.RSSerializers
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidInstance
import java.util.function.Function

class ExternalStorageTile : NetworkNodeTile<ExternalStorageNetworkNode?>(RSTiles.EXTERNAL_STORAGE) {
    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): ExternalStorageNetworkNode {
        return ExternalStorageNetworkNode(world, pos)
    }

    companion object {
        val PRIORITY: TileDataParameter<Int, ExternalStorageTile> = IPrioritizable.Companion.createParameter()
        val COMPARE: TileDataParameter<Int, ExternalStorageTile> = IComparable.Companion.createParameter()
        val WHITELIST_BLACKLIST: TileDataParameter<Int, ExternalStorageTile> = IWhitelistBlacklist.Companion.createParameter()
        val TYPE: TileDataParameter<Int, ExternalStorageTile> = IType.Companion.createParameter()
        val ACCESS_TYPE: TileDataParameter<AccessType?, ExternalStorageTile> = IAccessType.Companion.createParameter()
        val STORED: TileDataParameter<Long, ExternalStorageTile> = TileDataParameter<T, E>(RSSerializers.LONG_SERIALIZER, 0L, Function<E, T> { t: E ->
            var stored: Long = 0
            for (storage in t.getNode().getItemStorages()) {
                stored += storage.getStored().toLong()
            }
            for (storage in t.getNode().getFluidStorages()) {
                stored += storage.getStored().toLong()
            }
            stored
        })
        val CAPACITY: TileDataParameter<Long, ExternalStorageTile> = TileDataParameter<T, E>(RSSerializers.LONG_SERIALIZER, 0L, Function<E, T> { t: E ->
            var capacity: Long = 0
            for (storage in t.getNode().getItemStorages()) {
                capacity += storage.getCapacity()
            }
            for (storage in t.getNode().getFluidStorages()) {
                capacity += storage.getCapacity()
            }
            capacity
        })
    }

    init {
        dataManager.addWatchedParameter(PRIORITY)
        dataManager.addWatchedParameter(COMPARE)
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST)
        dataManager.addWatchedParameter(STORED)
        dataManager.addWatchedParameter(CAPACITY)
        dataManager.addWatchedParameter(TYPE)
        dataManager.addWatchedParameter(ACCESS_TYPE)
    }
}