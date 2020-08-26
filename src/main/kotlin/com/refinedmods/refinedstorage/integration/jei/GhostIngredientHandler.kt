package com.refinedmods.refinedstorage.integration.jei

import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot
import com.refinedmods.refinedstorage.screen.BaseScreen
import mezz.jei.api.gui.handlers.IGhostIngredientHandler
import net.minecraft.client.renderer.Rectangle2d
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidInstance
import java.util.*

class GhostIngredientHandler : IGhostIngredientHandler<BaseScreen<*>?> {
    fun <I> getTargets(gui: BaseScreen<*>, ingredient: I, doStart: Boolean): List<Target<I>> {
        val targets: MutableList<Target<I>> = ArrayList<Target<I>>()
        for (slot in gui.getContainer().inventorySlots) {
            if (!slot.isEnabled()) {
                continue
            }
            val bounds = Rectangle2d(gui.getGuiLeft() + slot.xPos, gui.getGuiTop() + slot.yPos, 17, 17)
            if (ingredient is ItemStack) {
                if (slot is LegacyFilterSlot || slot is FilterSlot) {
                    targets.add(object : Target<I>() {
                        val area: Rectangle2d
                            get() = bounds

                        fun accept(ingredient: I) {
                            slot.putStack(ingredient as ItemStack)

                            // RS.INSTANCE.network.sendToServer(new MessageSlotFilterSet(slot.slotNumber, (ItemStack) ingredient));
                        }
                    })
                }
            } else if (ingredient is FluidInstance) {
                if (slot is FluidFilterSlot) {
                    targets.add(object : Target<I>() {
                        val area: Rectangle2d
                            get() = bounds

                        fun accept(ingredient: I) {
                            // RS.INSTANCE.network.sendToServer(new MessageSlotFilterSetFluid(slot.slotNumber, StackUtils.copy((FluidInstance) ingredient, Fluid.BUCKET_VOLUME)));
                        }
                    })
                }
            }
        }
        return targets
    }

    fun onComplete() {
        // NO OP
    }
}