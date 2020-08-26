package com.refinedmods.refinedstorage.item.capabilityprovider

import com.refinedmods.refinedstorage.energy.ItemEnergyStorage
import net.minecraft.item.ItemStack
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage

class EnergyCapabilityProvider(stack: ItemStack?, energyCapacity: Int) : ICapabilityProvider {
    private val capability: LazyOptional<IEnergyStorage>
    @Nonnull
    fun <T> getCapability(@Nonnull cap: Capability<T>, @Nullable dire: Direction?): LazyOptional<T> {
        return if (cap === CapabilityEnergy.ENERGY) {
            capability.cast()
        } else LazyOptional.empty()
    }

    init {
        capability = LazyOptional.of({ ItemEnergyStorage(stack!!, energyCapacity) })
    }
}