package com.raoulvdberge.refinedstorage.screen.grid.stack;

import com.raoulvdberge.refinedstorage.api.storage.tracker.StorageTrackerEntry;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

public interface IGridStack {
    UUID getId();

    String getName();

    String getModId();

    String getModName();

    Set<String> getTags();

    String getTooltip();

    int getQuantity();

    String getFormattedFullQuantity();

    void draw(BaseScreen gui, int x, int y);

    Object getIngredient();

    @Nullable
    StorageTrackerEntry getTrackerEntry();

    void setTrackerEntry(@Nullable StorageTrackerEntry entry);

    boolean isCraftable();
}
