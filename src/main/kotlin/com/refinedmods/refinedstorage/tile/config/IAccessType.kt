package com.refinedmods.refinedstorage.tile.config

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.tile.data.RSSerializers
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.tileentity.BlockEntity
import java.util.function.BiConsumer
import java.util.function.Function

interface IAccessType {
    var accessType: AccessType?

    companion object {
        fun <T> createParameter(): TileDataParameter<AccessType?, T> where T : BlockEntity?, T : INetworkNodeProxy<*>? {
            return TileDataParameter<T?, E>(RSSerializers.ACCESS_TYPE_SERIALIZER, AccessType.INSERT_EXTRACT, Function<E, T?> { t: E -> (t.getNode() as IAccessType).accessType }, BiConsumer<E, T?> { t: E, v: T? -> (t.getNode() as IAccessType).accessType = v })
        }
    }
}