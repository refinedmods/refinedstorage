package com.refinedmods.refinedstorage.energy

import net.minecraftforge.energy.EnergyStorage

class BaseEnergyStorage(capacity: Int, maxReceive: Int, maxExtract: Int) : EnergyStorage(capacity, maxReceive, maxExtract) {
    // @Volatile: Impl from EnergyStorage#extractEnergy, without the canExtract check
    fun extractEnergyBypassCanExtract(maxExtract: Int, simulate: Boolean) {
        val energyExtracted = Math.min(energy, maxExtract)
        if (!simulate) {
            energy -= energyExtracted
        }
    }

    fun setStored(energy: Int) {
        energy = energy
    }
}