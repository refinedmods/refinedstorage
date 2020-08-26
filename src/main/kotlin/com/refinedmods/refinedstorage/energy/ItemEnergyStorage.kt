package com.refinedmods.refinedstorage.energy

import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.energy.EnergyStorage

class ItemEnergyStorage(private val stack: ItemStack, capacity: Int) : EnergyStorage(capacity, Int.MAX_VALUE, Int.MAX_VALUE) {
    fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        val received: Int = super.receiveEnergy(maxReceive, simulate)
        if (received > 0 && !simulate) {
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            stack.tag!!.putInt(NBT_ENERGY, getEnergyStored())
        }
        return received
    }

    fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        val extracted: Int = super.extractEnergy(maxExtract, simulate)
        if (extracted > 0 && !simulate) {
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            stack.tag!!.putInt(NBT_ENERGY, getEnergyStored())
        }
        return extracted
    }

    companion object {
        private const val NBT_ENERGY = "Energy"
    }

    init {
        this.energy = if (stack.hasTag() && stack.tag!!.contains(NBT_ENERGY)) stack.tag!!.getInt(NBT_ENERGY) else 0
    }
}