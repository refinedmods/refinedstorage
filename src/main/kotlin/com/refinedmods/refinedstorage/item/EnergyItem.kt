package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.item.capabilityprovider.EnergyCapabilityProvider
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import java.util.function.Supplier

abstract class EnergyItem(properties: Item.Properties?, private val creative: Boolean, private val energyCapacity: Supplier<Int>) : Item(properties) {
    fun initCapabilities(stack: ItemStack?, tag: CompoundTag?): ICapabilityProvider {
        return EnergyCapabilityProvider(stack, energyCapacity.get())
    }

    fun showDurabilityBar(stack: ItemStack?): Boolean {
        return !creative
    }

    fun getDurabilityForDisplay(stack: ItemStack): Double {
        val energy: IEnergyStorage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null) ?: return 0
        return 1.0 - energy.getEnergyStored() as Double / energy.getMaxEnergyStored() as Double
    }

    fun getRGBDurabilityForDisplay(stack: ItemStack): Int {
        val energy: IEnergyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null)
                ?: return super.getRGBDurabilityForDisplay(stack)
        return MathHelper.hsvToRGB(Math.max(0.0f, energy.getEnergyStored() as Float / energy.getMaxEnergyStored() as Float) / 3.0f, 1.0f, 1.0f)
    }

    open fun addInformation(stack: ItemStack, @Nullable world: World?, tooltip: MutableList<Text?>, flag: ITooltipFlag?) {
        super.addInformation(stack, world, tooltip, flag)
        if (!creative) {
            stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent({ energy -> tooltip.add(TranslationTextComponent("misc.refinedstorage.energy_stored", energy.getEnergyStored(), energy.getMaxEnergyStored()).setStyle(Styles.GRAY)) })
        }
    }
}