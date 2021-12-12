package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.util.RenderUtils;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiContainerHandler implements IGuiContainerHandler<ContainerScreen<?>> {
    @Override
    public List<Rectangle2d> getGuiExtraAreas(ContainerScreen<?> screen) {
        if (screen instanceof BaseScreen) {
            List<SideButton> sideButtons = ((BaseScreen) screen).getSideButtons();

            List<Rectangle2d> rectangles = new ArrayList<>();
            for (SideButton sideButton : sideButtons) {
                rectangles.add(new Rectangle2d(sideButton.x, sideButton.y, sideButton.getWidth(), sideButton.getHeight()));
            }

            return rectangles;
        }

        return Collections.emptyList();
    }

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

        if (screen.getMenu() instanceof BaseContainer) {
            for (FluidFilterSlot slot : ((BaseContainer) screen.getMenu()).getFluidSlots()) {
                FluidStack fluidInSlot = slot.getFluidInventory().getFluid(slot.getSlotIndex());

                if (!fluidInSlot.isEmpty() && RenderUtils.inBounds(slot.x, slot.y, 18, 18, mouseX, mouseY)) {
                    return fluidInSlot;
                }
            }
        }

        return null;
    }
}
