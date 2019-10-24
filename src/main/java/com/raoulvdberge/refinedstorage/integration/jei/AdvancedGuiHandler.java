package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.container.ContainerBase;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class AdvancedGuiHandler implements IAdvancedGuiHandler<GuiBase> {
    @Override
    public Class<GuiBase> getGuiContainerClass() {
        return GuiBase.class;
    }

    @Nullable
    @Override
    public List<Rectangle> getGuiExtraAreas(GuiBase gui) {
        return null;
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(GuiBase gui, int mouseX, int mouseY) {
        mouseX -= gui.getGuiLeft();
        mouseY -= gui.getGuiTop();

        if (gui instanceof GuiGrid) {
            GuiGrid grid = (GuiGrid) gui;

            if (grid.getScrollbar() != null && !grid.getSearchField().isFocused() && grid.isOverSlotArea(mouseX, mouseY)) {
                return grid.getSlotNumber() >= 0 && grid.getSlotNumber() < grid.getView().getStacks().size() ? grid.getView().getStacks().get(grid.getSlotNumber()).getIngredient() : null;
            }
        }

        if (gui.inventorySlots instanceof ContainerBase) {
            for (SlotFilterFluid slot : ((ContainerBase) gui.inventorySlots).getFluidSlots()) {
                FluidStack fluidInSlot = slot.getFluidInventory().getFluid(slot.getSlotIndex());

                if (fluidInSlot != null && gui.isMouseOverSlotPublic(slot, mouseX + gui.getGuiLeft(), mouseY + gui.getGuiTop())) {
                    return fluidInSlot;
                }
            }
        }

        return null;
    }
}
