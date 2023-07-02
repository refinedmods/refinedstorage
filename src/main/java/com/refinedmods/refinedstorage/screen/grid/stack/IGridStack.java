package com.refinedmods.refinedstorage.screen.grid.stack;

import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IGridStack {
    UUID getId();

    @Nullable
    UUID getOtherId();

    void updateOtherId(@Nullable UUID otherId);

    String getName();

    String getModId();

    String getModName();

    Set<String> getTags();

    List<Component> getTooltip(boolean bypassCache);

    int getQuantity();

    void setQuantity(int amount);

    String getFormattedFullQuantity();

    void draw(GuiGraphics graphics, BaseScreen<?> screen, int x, int y);

    Object getIngredient();

    @Nullable
    StorageTrackerEntry getTrackerEntry();

    void setTrackerEntry(@Nullable StorageTrackerEntry entry);

    boolean isCraftable();
}
