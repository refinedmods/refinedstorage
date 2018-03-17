package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerConstructor;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonCompare;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonConstuctorDrop;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonType;
import com.raoulvdberge.refinedstorage.tile.TileConstructor;

public class GuiConstructor extends GuiBase {
    public GuiConstructor(ContainerConstructor container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileConstructor.REDSTONE_MODE));

        addSideButton(new SideButtonType(this, TileConstructor.TYPE));

        addSideButton(new SideButtonCompare(this, TileConstructor.COMPARE, IComparer.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(this, TileConstructor.COMPARE, IComparer.COMPARE_NBT));
        addSideButton(new SideButtonCompare(this, TileConstructor.COMPARE, IComparer.COMPARE_OREDICT));
        addSideButton(new SideButtonConstuctorDrop(this));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/constructor.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:constructor"));
        drawString(7, 43, t("container.inventory"));
    }
}
