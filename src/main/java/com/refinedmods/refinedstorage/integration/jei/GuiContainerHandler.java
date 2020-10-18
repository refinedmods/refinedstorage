package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.util.RenderUtils;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class GuiContainerHandler implements IGuiContainerHandler<ContainerScreen<?>> {
    @Nullable
    @Override
    public Object getIngredientUnderMouse(ContainerScreen screen, double mouseX, double mouseY) {
        mouseX -= screen.getGuiLeft();
        mouseY -= screen.getGuiTop();

        if (screen instanceof GridScreen) {
            GridScreen grid = (GridScreen) screen;

            if (!grid.getSearchField().isFocused() && grid.isOverSlotArea(mouseX, mouseY)) {
                boolean inRange = grid.getSlotNumber() >= 0 && grid.getSlotNumber() < grid.getView().getStacks().size();

                return inRange ? grid.getView().getStacks().get(grid.getSlotNumber()).getIngredient() : null;
            }
        }

        if (screen.getContainer() instanceof BaseContainer) {
            for (FluidFilterSlot slot : ((BaseContainer) screen.getContainer()).getFluidSlots()) {
                FluidStack fluidInSlot = slot.getFluidInventory().getFluid(slot.getSlotIndex());

                if (!fluidInSlot.isEmpty() && RenderUtils.inBounds(slot.xPos, slot.yPos, 18, 18, mouseX, mouseY)) {
                    return fluidInSlot;
                }
            }
        }

        return null;
    }
}
