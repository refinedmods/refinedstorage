package com.refinedmods.refinedstorage.item.property

import com.refinedmods.refinedstorage.apiimpl.network.Network
import com.refinedmods.refinedstorage.block.ControllerBlock
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.IItemPropertyGetter
import net.minecraft.item.ItemStack
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage

class ControllerItemPropertyGetter : IItemPropertyGetter {
    fun call(stack: ItemStack, @Nullable p_call_2_: ClientWorld?, @Nullable p_call_3_: LivingEntity?): Float {
        val storage: IEnergyStorage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null)
        return if (storage != null) {
            Network.energyType.ordinal()
        } else ControllerBlock.EnergyType.OFF.ordinal.toFloat()
    }
}