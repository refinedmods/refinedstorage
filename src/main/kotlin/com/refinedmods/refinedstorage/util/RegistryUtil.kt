package com.refinedmods.refinedstorage.util;

import net.minecraft.util.Identifier
import net.minecraft.util.registry.DefaultedRegistry

object RegistryUtil {
    inline fun <TOut, T : TOut> DefaultedRegistry<T>.getOr(id: Identifier, block: () -> TOut): TOut {
        val item = getOrEmpty(id)
        return if (item.isPresent)
            item.get()
        else
            block()
    }

}