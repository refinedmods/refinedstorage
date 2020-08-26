package com.refinedmods.refinedstorage.item.blockitem

import com.refinedmods.refinedstorage.block.BaseBlock
import com.refinedmods.refinedstorage.item.capabilityprovider.EnergyCapabilityProvider
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import reborncore.client.gui.builder.widget.tooltip.ToolTip
import java.util.function.Supplier

abstract class EnergyBlockItem(block: BaseBlock, properties: Item.Settings, private val creative: Boolean, private val energyCapacity: Supplier<Int>):
        BaseBlockItem(block, properties)
{
    // TODO Replace capability
//    fun initCapabilities(stack: ItemStack?, tag: CompoundTag?): ICapabilityProvider {
//        return EnergyCapabilityProvider(stack, energyCapacity.get())
//    }

    fun showDurabilityBar(stack: ItemStack): Boolean {
        return !creative
    }

    fun getDurabilityForDisplay(stack: ItemStack): Double {
        // TODO energy
//        val energy: IEnergyStorage = stack.getCapability(CapabilityEnergy.ENERGY).orElse(null) ?: return 0
//        return 1.0 - energy.getEnergyStored() as Double / energy.getMaxEnergyStored() as Double

        return 0.0
    }


    fun getRGBDurabilityForDisplay(stack: ItemStack): Int {
        // TODO energy
//        val energy: IEnergyStorage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null)
//                ?: return super.getRGBDurabilityForDisplay(stack)
//        return MathHelper.hsvToRGB(Math.max(0.0f, energy.getEnergyStored() as Float / energy.getMaxEnergyStored() as Float) / 3.0f, 1.0f, 1.0f)
        return 0
    }

    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text>?, context: TooltipContext?) {
        super.appendTooltip(stack, world, tooltip, context)
        if (!creative) {
            // TODO Energy
//            stack.getCapability(CapabilityEnergy.ENERGY, null).ifPresent({ energy -> tooltip.add(TranslationTextComponent("misc.refinedstorage.energy_stored", energy.getEnergyStored(), energy.getMaxEnergyStored()).setStyle(Styles.GRAY)) })
        }
    }

}