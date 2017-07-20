package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerExporter;
import com.raoulvdberge.refinedstorage.gui.sidebutton.*;
import com.raoulvdberge.refinedstorage.integration.forestry.IntegrationForestry;
import com.raoulvdberge.refinedstorage.tile.TileExporter;

public class GuiExporter extends GuiBase {
    public GuiExporter(ContainerExporter container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileExporter.REDSTONE_MODE));

        addSideButton(new SideButtonType(this, TileExporter.TYPE));

        addSideButton(new SideButtonCompare(this, TileExporter.COMPARE, IComparer.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(this, TileExporter.COMPARE, IComparer.COMPARE_NBT));
        addSideButton(new SideButtonCompare(this, TileExporter.COMPARE, IComparer.COMPARE_OREDICT));
        if(IntegrationForestry.isLoaded()) {
        	addSideButton(new SideButtonCompare(this, TileExporter.COMPARE, IComparer.COMPARE_FORESTRY));
        }

        addSideButton(new SideButtonExporterRegulator(this));
        addSideButton(new SideButtonExporterCraftOnly(this));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/exporter.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:exporter"));
        drawString(7, 43, t("container.inventory"));
    }
}
