package com.raoulvdberge.refinedstorage.gui.grid.stack;

import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.gui.GuiBase;

import javax.annotation.Nullable;

public interface IGridStack {
    int getHash();

    String getName();

    String getModId();

    String getModName();

    String[] getOreIds();

    String getTooltip();

    int getQuantity();

    void setQuantity(int amount);

    String getFormattedFullQuantity();

    void draw(GuiBase gui, int x, int y);

    Object getIngredient();

    @Nullable
    IStorageTracker.IStorageTrackerEntry getTrackerEntry();

    void setTrackerEntry(@Nullable IStorageTracker.IStorageTrackerEntry entry);

    boolean isCraftable();

    boolean doesDisplayCraftText();
}
