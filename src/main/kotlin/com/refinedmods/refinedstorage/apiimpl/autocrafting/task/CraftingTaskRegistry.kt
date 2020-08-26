package com.refinedmods.refinedstorage.apiimpl.autocrafting.task

import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskRegistry
import net.minecraft.util.Identifierimport

class CraftingTaskRegistry : ICraftingTaskRegistry {
    private val registry: MutableMap<Identifier?, ICraftingTaskFactory?> = HashMap<Identifier?, ICraftingTaskFactory?>()
    fun add(id: Identifier?, factory: ICraftingTaskFactory?) {
        registry[id] = factory
    }

    @Nullable
    operator fun get(id: Identifier?): ICraftingTaskFactory? {
        return registry[id]
    }
}