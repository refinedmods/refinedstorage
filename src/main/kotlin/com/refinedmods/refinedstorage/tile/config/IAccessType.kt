package com.refinedmods.refinedstorage.tile.config

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import java.util.function.BiConsumer
import java.util.function.Function

interface IAccessType {
    var accessType: AccessType?

    companion object {
        fun <E> createParameter(): TileDataParameter<Int?, E> where E : BlockEntity?, E: INetworkNodeProxy<*>? {
            return TileDataParameter<Int?, E>(
                    AccessType.INSERT_EXTRACT.ordinal,
                    TrackedDataHandlerRegistry.INTEGER,
                    Function { t: E? -> (t?.node as IAccessType).accessType?.ordinal },
                    BiConsumer {
                        t: E?, v: Int? ->
                        (t?.node as IAccessType).accessType = v?.let { ordinal ->
                             AccessType.values()[ordinal]
                        }
                    }
            )
        }
    }
}