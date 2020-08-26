package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType
import com.refinedmods.refinedstorage.tile.config.IAccessType
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IPrioritizable
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.tile.data.RSSerializers
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.util.StorageBlockUtils
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Function

class StorageTile(type: ItemStorageType) : NetworkNodeTile<StorageNetworkNode?>(StorageBlockUtils.getBlockEntityType(type)) {
    private val type: ItemStorageType
    val itemStorageType: ItemStorageType
        get() = type

    @Nonnull
    override fun createNode(world: World?, pos: BlockPos?): StorageNetworkNode {
        return StorageNetworkNode(world, pos, type)
    }

    companion object {
        val PRIORITY: TileDataParameter<Int, StorageTile> = IPrioritizable.Companion.createParameter()
        val COMPARE: TileDataParameter<Int, StorageTile> = IComparable.Companion.createParameter()
        val WHITELIST_BLACKLIST: TileDataParameter<Int, StorageTile> = IWhitelistBlacklist.Companion.createParameter()
        val ACCESS_TYPE: TileDataParameter<AccessType?, StorageTile> = IAccessType.Companion.createParameter()
        val STORED: TileDataParameter<Long, StorageTile> = TileDataParameter<T, E>(RSSerializers.LONG_SERIALIZER, 0L, Function<E, T> { t: E -> if (t.getNode().getStorage() != null) t.getNode().getStorage().getStored() else 0 })
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