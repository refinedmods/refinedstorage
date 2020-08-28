package com.refinedmods.refinedstorage.apiimpl.util

import com.refinedmods.refinedstorage.api.util.IFilter
import reborncore.common.fluid.container.FluidInstance


class FluidFilter(
        override val stack: FluidInstance,
        override val compare: Int,
        override val mode: Int,
        override val isModFilter: Boolean
) : IFilter<FluidInstance?>