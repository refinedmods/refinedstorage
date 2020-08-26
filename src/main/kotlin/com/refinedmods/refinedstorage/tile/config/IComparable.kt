package com.refinedmods.refinedstorage.tile.config

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.tileentity.BlockEntity
import java.util.function.BiConsumer
import java.util.function.Function

interface IComparable {
    var compare: Int

    companion object {
        fun <T> createParameter(): TileDataParameter<Int, T> where T : BlockEntity?, T : INetworkNodeProxy<*>? {
            return TileDataParameter<Int, T>(DataSerializers.VARINT, 0, Function { t: T -> (t!!.node as IComparable).compare }, BiConsumer { t: T, v: Int -> (t!!.node as IComparable).compare = v })
        }
    }
}