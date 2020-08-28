package com.refinedmods.refinedstorage.tile.data

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.data.TrackedDataHandler
import java.util.function.BiConsumer

class TileDataParameter<T: Any?, E : BlockEntity?>(
        var value: T?,
        val serializer: TrackedDataHandler<T?>,
        val valueProducer: java.util.function.Function<E?, T?>,
        val valueConsumer: BiConsumer<E?, T?>? = null,
        val listener: TileDataParameterClientListener<T?>? = null
) {
    var id = 0

    fun setValue(initial: Boolean, value: T?) {

        this.value = value
        listener?.onChanged(initial, value)
    }

}