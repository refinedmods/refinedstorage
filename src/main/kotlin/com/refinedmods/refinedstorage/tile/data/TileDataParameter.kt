package com.refinedmods.refinedstorage.tile.data

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.data.TrackedDataHandler

class TileDataParameter<T, E : BlockEntity>(
        val serializer: TrackedDataHandler<T>,
        var value: T,
        val listener: TileDataParameterClientListener<T>? = null
) {
    var id = 0

    fun setValue(initial: Boolean, value: T) {

        this.value = value
        listener?.onChanged(initial, value)
    }

}