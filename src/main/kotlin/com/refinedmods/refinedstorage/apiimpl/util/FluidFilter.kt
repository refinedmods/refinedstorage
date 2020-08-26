package com.refinedmods.refinedstorage.apiimpl.util

import com.refinedmods.refinedstorage.api.util.IFilter
import net.minecraftforge.fluids.FluidInstance


class FluidFilter(stack: FluidInstance, compare: Int, mode: Int, modFilter: Boolean) : IFilter<FluidInstance?> {
    private override val stack: FluidInstance
    override val compare: Int
    override val mode: Int
    override val isModFilter: Boolean
    override fun getStack(): FluidInstance {
        return stack
    }

    init {
        this.stack = stack
        this.compare = compare
        this.mode = mode
        isModFilter = modFilter
    }
}