package com.refinedmods.refinedstorage.screen.grid.stack;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.util.text.ITextComponent;

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

    List<ITextComponent> getTooltip(boolean bypassCache);

    int getQuantity();

    void setQuantity(int amount);

    String getFormattedFullQuantity();

    void draw(MatrixStack matrixStack, BaseScreen<?> screen, int x, int y);

    Object getIngredient();

    @Nullable
    StorageTrackerEntry getTrackerEntry();

    void setTrackerEntry(@Nullable StorageTrackerEntry entry);

    boolean isCraftable();
}
