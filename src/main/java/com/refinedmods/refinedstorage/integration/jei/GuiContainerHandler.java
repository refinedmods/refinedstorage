package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.container.BaseContainerMenu;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.refinedmods.refinedstorage.util.RenderUtils;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GuiContainerHandler implements IGuiContainerHandler<AbstractContainerScreen<?>> {
    private final JeiHelper jeiHelper;

    public GuiContainerHandler(JeiHelper jeiHelper) {
        this.jeiHelper = jeiHelper;
    }

    @Override
    public List<Rect2i> getGuiExtraAreas(AbstractContainerScreen<?> screen) {
        if (screen instanceof BaseScreen) {
            List<SideButton> sideButtons = ((BaseScreen) screen).getSideButtons();

            List<Rect2i> rectangles = new ArrayList<>();
            for (SideButton sideButton : sideButtons) {
                rectangles.add(new Rect2i(sideButton.getX(), sideButton.getY(), sideButton.getWidth(), sideButton.getHeight()));
            }

            return rectangles;
        }

        return Collections.emptyList();
    }

    @Override
    public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(AbstractContainerScreen screen, double mouseX, double mouseY) {
        mouseX -= screen.getGuiLeft();
        mouseY -= screen.getGuiTop();

        if (screen instanceof GridScreen) {
            GridScreen grid = (GridScreen) screen;

            if (!grid.getSearchField().isFocused() && grid.isOverSlotArea(mouseX, mouseY)) {
                boolean inRange = grid.getSlotNumber() >= 0 && grid.getSlotNumber() < grid.getView().getStacks().size();
                Rect2i area = new Rect2i(grid.getSlotNumberX(), grid.getSlotNumberY(), 18, 18);
                return inRange
                    ? jeiHelper.makeClickableIngredient(grid.getView().getStacks().get(grid.getSlotNumber()).getIngredient(), area)
                    : Optional.empty();
            }
        }

        if (screen.getMenu() instanceof BaseContainerMenu) {
            for (FluidFilterSlot slot : ((BaseContainerMenu) screen.getMenu()).getFluidSlots()) {
                FluidStack fluidInSlot = slot.getFluidInventory().getFluid(slot.getSlotIndex());

                if (!fluidInSlot.isEmpty() && RenderUtils.inBounds(slot.x, slot.y, 18, 18, mouseX, mouseY)) {
                    Rect2i area = new Rect2i(slot.x, slot.y, 18, 18);
                    return jeiHelper.makeClickableIngredient(fluidInSlot, area);
                }
            }
        }

        return Optional.empty();
    }
}
