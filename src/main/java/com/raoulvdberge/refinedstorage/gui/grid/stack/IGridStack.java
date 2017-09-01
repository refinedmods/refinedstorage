package com.raoulvdberge.refinedstorage.gui.grid.stack;

import com.raoulvdberge.refinedstorage.gui.GuiBase;

public interface IGridStack {
    int getHash();

    String getName();

    String getModId();

    String[] getOreIds();

    String getTooltip(boolean quantity);

    int getQuantity();

    void draw(GuiBase gui, int x, int y);

    Object getIngredient();
}
