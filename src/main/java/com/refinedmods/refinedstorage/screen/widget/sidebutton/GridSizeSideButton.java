package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GridSizeSideButton extends SideButton {
    private final Supplier<Integer> sizeSupplier;
    private final Consumer<Integer> listener;

    public GridSizeSideButton(BaseScreen screen, Supplier<Integer> sizeSupplier, Consumer<Integer> listener) {
        super(screen);
        this.sizeSupplier = sizeSupplier;
        this.listener = listener;
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.refinedstorage.grid.size") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.grid.size." + this.sizeSupplier.get());
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        int size = this.sizeSupplier.get();

        int tx = 0;

        if (size == IGrid.SIZE_STRETCH) {
            tx = 48;
        } else if (size == IGrid.SIZE_SMALL) {
            tx = 0;
        } else if (size == IGrid.SIZE_MEDIUM) {
            tx = 16;
        } else if (size == IGrid.SIZE_LARGE) {
            tx = 32;
        }

        graphics.blit(BaseScreen.ICONS_TEXTURE, x, y, 64 + tx, 64, 16, 16);
    }

    @Override
    public void onPress() {
        int size = this.sizeSupplier.get();

        if (size == IGrid.SIZE_STRETCH) {
            size = IGrid.SIZE_SMALL;
        } else if (size == IGrid.SIZE_SMALL) {
            size = IGrid.SIZE_MEDIUM;
        } else if (size == IGrid.SIZE_MEDIUM) {
            size = IGrid.SIZE_LARGE;
        } else if (size == IGrid.SIZE_LARGE) {
            size = IGrid.SIZE_STRETCH;
        }

        this.listener.accept(size);
    }
}
