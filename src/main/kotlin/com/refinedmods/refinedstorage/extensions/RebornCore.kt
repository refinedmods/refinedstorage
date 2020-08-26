package com.refinedmods.refinedstorage.extensions

import net.minecraft.fluid.Fluids
import net.minecraft.nbt.CompoundTag
import reborncore.common.fluid.FluidValue
import reborncore.common.fluid.container.FluidInstance
import reborncore.common.util.Tank

fun FluidValue.safeAdd(other: FluidValue): FluidValue {
    val total = rawValue.toLong() + other.rawValue.toLong()
    return when {
        total > Int.MAX_VALUE -> { FluidValue.fromRaw(Int.MAX_VALUE) }
        total < 0 -> { FluidValue.EMPTY }
        else -> { FluidValue.fromRaw(total.toInt()) }
    }
}

fun FluidValue.safeSubtract(other: FluidValue): FluidValue {
    return this.safeAdd(FluidValue.fromRaw(0 - other.rawValue))
}

fun Tank.transferFrom(source: Tank, amount: FluidValue, simulate: Boolean): FluidValue {
    // If the source is empty, there's nothing to move...
    if (source.fluid != Fluids.EMPTY && !source.fluidAmount.isEmpty) return source.fluidAmount

    // If the destination is full, there's nothing to move...
    if (this.isFull) return source.fluidAmount

    // If destination isn't empty, and the source isn't the same fluid, we can't move it...
    if (this.fluid != Fluids.EMPTY && source.fluid != this.fluid) return source.fluidAmount

    // Get the amount we CAN move
    val transferAmount = source.fluidAmount.min(amount).min(this.freeSpace)
    if (simulate) {
        return this.fluidAmount.add(transferAmount)
    }

    if (!transferAmount.isEmpty) {
        var fluidInstance = this.fluidInstance
        if (fluidInstance.isEmpty) {
            fluidInstance = FluidInstance(source.fluid, transferAmount)
        } else {
            fluidInstance.addAmount(transferAmount)
        }

        source.fluidAmount = source.fluidAmount.subtract(transferAmount)
        this.fluidInstance = fluidInstance
        if (source.fluidAmount == FluidValue.EMPTY) {
            source.fluid = Fluids.EMPTY
        }
    }

    return source.fluidAmount
}