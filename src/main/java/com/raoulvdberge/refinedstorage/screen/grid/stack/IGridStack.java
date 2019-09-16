package com.raoulvdberge.refinedstorage.screen.grid.stack;

import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;

import javax.annotation.Nullable;

public interface IGridStack {
    int getHash();

    String getName();

    String getModId();

    String getModName();

    String[] getOreIds();

    String getTooltip();

    int getQuantity();

    String getFormattedFullQuantity();

    void draw(BaseScreen gui, int x, int y);

    Object getIngredient();

    @Nullable
    IStorageTracker.IStorageTrackerEntry getTrackerEntry();

    void setTrackerEntry(@Nullable IStorageTracker.IStorageTrackerEntry entry);

    boolean isCraftable();

    boolean doesDisplayCraftText();

    void setDisplayCraftText(boolean displayCraftText);
}
