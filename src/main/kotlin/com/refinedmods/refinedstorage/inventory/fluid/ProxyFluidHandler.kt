package com.refinedmods.refinedstorage.inventory.fluid

import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank

class ProxyFluidHandler(insertHandler: FluidTank, extractHandler: FluidTank) : IFluidHandler {
    private val insertHandler: FluidTank
    private val extractHandler: FluidTank
    fun getTanks(): Int {
        return 2
    }

    @Nonnull
    fun getFluidInTank(tank: Int): FluidInstance {
        return if (tank == 0) insertHandler.getFluidInTank(0) else extractHandler.getFluidInTank(0)
    }

    fun getTankCapacity(tank: Int): Int {
        return if (tank == 0) insertHandler.getTankCapacity(0) else extractHandler.getTankCapacity(0)
    }

    fun isFluidValid(tank: Int, @Nonnull stack: FluidInstance?): Boolean {
        return if (tank == 0) insertHandler.isFluidValid(0, stack) else extractHandler.isFluidValid(0, stack)
    }

    fun fill(resource: FluidInstance?, action: FluidAction?): Int {
        return insertHandler.fill(resource, action)
    }

    @Nonnull
    fun drain(resource: FluidInstance?, action: FluidAction?): FluidInstance {
        return extractHandler.drain(resource, action)
    }

    @Nonnull
    fun drain(maxDrain: Int, action: FluidAction?): FluidInstance {
        return extractHandler.drain(maxDrain, action)
    }

    init {
        this.insertHandler = insertHandler
        this.extractHandler = extractHandler
    }
}