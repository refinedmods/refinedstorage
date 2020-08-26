package com.refinedmods.refinedstorage.integration.jei

import com.refinedmods.refinedstorage.container.BaseContainer
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.util.RenderUtils
import mezz.jei.api.gui.handlers.IGuiContainerHandler
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraftforge.fluids.FluidInstance

class GuiContainerHandler : IGuiContainerHandler<ContainerScreen<*>?> {
    @Nullable
    fun getIngredientUnderMouse(screen: ContainerScreen, mouseX: Double, mouseY: Double): Any? {
        var mouseX = mouseX
        var mouseY = mouseY
        mouseX -= screen.getGuiLeft()
        mouseY -= screen.getGuiTop()
        if (screen is GridScreen) {
            val grid = screen as GridScreen
            if (!grid.searchField.isFocused && grid.isOverSlotArea(mouseX, mouseY)) {
                return if (grid.slotNumber >= 0 && grid.slotNumber < grid.view.stacks.size) grid.view.stacks[grid.slotNumber].ingredient else null
            }
        }
        if (screen.getContainer() is BaseContainer) {
            for (slot in (screen.getContainer() as BaseContainer).getFluidSlots()) {
                val fluidInSlot: FluidInstance = slot.fluidInventory.getFluid(slot.getSlotIndex())
                if (!fluidInSlot.isEmpty() && RenderUtils.inBounds(slot.xPos, slot.yPos, 18, 18, mouseX, mouseY)) {
                    return fluidInSlot
                }
            }
        }
        return null
    }
}