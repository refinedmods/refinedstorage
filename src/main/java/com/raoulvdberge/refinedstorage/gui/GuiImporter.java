package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerImporter;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonCompare;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonMode;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonType;
import com.raoulvdberge.refinedstorage.integration.forestry.IntegrationForestry;
import com.raoulvdberge.refinedstorage.tile.TileImporter;

public class GuiImporter extends GuiBase {
    public GuiImporter(ContainerImporter container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileImporter.REDSTONE_MODE));

        addSideButton(new SideButtonType(this, TileImporter.TYPE));

        addSideButton(new SideButtonMode(this, TileImporter.MODE));

        addSideButton(new SideButtonCompare(this, TileImporter.COMPARE, IComparer.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(this, TileImporter.COMPARE, IComparer.COMPARE_NBT));
        addSideButton(new SideButtonCompare(this, TileImporter.COMPARE, IComparer.COMPARE_OREDICT));
        if(IntegrationForestry.isLoaded()) {
            addSideButton(new SideButtonCompare(this, TileImporter.COMPARE, IComparer.COMPARE_FORESTRY | IntegrationForestry.Tag.GEN.getFlag() | IntegrationForestry.Tag.IS_ANALYZED.getFlag()));
        }
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/importer.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:importer"));
        drawString(7, 43, t("container.inventory"));
    }
}
