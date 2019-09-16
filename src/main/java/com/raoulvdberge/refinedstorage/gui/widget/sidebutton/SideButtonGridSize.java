package com.raoulvdberge.refinedstorage.gui.widget.sidebutton;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SideButtonGridSize extends SideButton {
    private Supplier<Integer> size;
    private Consumer<Integer> handler;

    public SideButtonGridSize(GuiBase gui, Supplier<Integer> size, Consumer<Integer> handler) {
        super(gui);
        this.size = size;
        this.handler = handler;
    }

    @Override
    public String getTooltip() {
        return I18n.format("sidebutton.refinedstorage:grid.size") + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage:grid.size." + this.size.get());
    }

    @Override
    protected void drawButtonIcon(int x, int y) {
        int size = this.size.get();

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

        gui.blit(x, y, 64 + tx, 64, 16, 16);
    }

    @Override
    public void onPress() {
        int size = this.size.get();

        if (size == IGrid.SIZE_STRETCH) {
            size = IGrid.SIZE_SMALL;
        } else if (size == IGrid.SIZE_SMALL) {
            size = IGrid.SIZE_MEDIUM;
        } else if (size == IGrid.SIZE_MEDIUM) {
            size = IGrid.SIZE_LARGE;
        } else if (size == IGrid.SIZE_LARGE) {
            size = IGrid.SIZE_STRETCH;
        }

        this.handler.accept(size);
    }
}
