package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import mezz.jei.api.gui.IAdvancedGuiHandler;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class GuiHandlerGrid implements IAdvancedGuiHandler<GuiGrid> {
    @Override
    public Class<GuiGrid> getGuiContainerClass() {
        return GuiGrid.class;
    }

    @Nullable
    @Override
    public List<Rectangle> getGuiExtraAreas(GuiGrid gui) {
        return null;
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(GuiGrid gui, int mouseX, int mouseY) {
        mouseX -= gui.getGuiLeft();
        mouseY -= gui.getGuiTop();

        if (gui.getScrollbar() != null && !gui.getSearchField().isFocused() && gui.isOverSlotArea(mouseX, mouseY)) {
            return gui.getSlotNumber() >= 0 && gui.getSlotNumber() < GuiGrid.STACKS.size() ? GuiGrid.STACKS.get(gui.getSlotNumber()).getIngredient() : null;
        }

        return null;
    }
}
