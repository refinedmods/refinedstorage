package com.refinedmods.refinedstorage.render

import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.util.Identifier
import java.util.*

class BakedModelOverrideRegistry {
    interface BakedModelOverrideFactory {
        fun create(base: IBakedModel?, registry: Map<Identifier?, IBakedModel?>?): IBakedModel?
    }

    private val registry: MutableMap<Identifier, BakedModelOverrideFactory> = HashMap<Identifier, BakedModelOverrideFactory>()
    fun add(id: Identifier, factory: BakedModelOverrideFactory) {
        registry[id] = factory
    }

    @Nullable
    operator fun get(id: Identifier?): BakedModelOverrideFactory? {
        return registry[id]
    }
}