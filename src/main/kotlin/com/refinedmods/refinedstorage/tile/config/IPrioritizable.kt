package com.refinedmods.refinedstorage.tile.config

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import java.util.function.BiConsumer
import java.util.function.Function

interface IPrioritizable {
    var priority: Int

    companion object {
        fun <T> createParameter(): TileDataParameter<Int, T> where T : BlockEntity?, T : INetworkNodeProxy<*>? {
            return TileDataParameter<Int, T>(
                    0,
                    TrackedDataHandlerRegistry.INTEGER,
                    Function { t: T? -> (t!!.node as IPrioritizable).priority },
                    BiConsumer { t: T?, v: Int? -> (t!!.node as IPrioritizable).priority = v!! }
            )
        }
    }
}