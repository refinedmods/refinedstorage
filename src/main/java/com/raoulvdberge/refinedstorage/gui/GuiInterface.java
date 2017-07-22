package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerInterface;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonCompare;
import com.raoulvdberge.refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.integration.forestry.IntegrationForestry;
import com.raoulvdberge.refinedstorage.tile.TileInterface;

public class GuiInterface extends GuiBase {
    public GuiInterface(ContainerInterface container) {
        super(container, 211, 217);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileInterface.REDSTONE_MODE));

        addSideButton(new SideButtonCompare(this, TileInterface.COMPARE, IComparer.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(this, TileInterface.COMPARE, IComparer.COMPARE_NBT));
        addSideButton(new SideButtonCompare(this, TileInterface.COMPARE, IComparer.COMPARE_OREDICT));
        if(IntegrationForestry.isLoaded()) {
        	addSideButton(new SideButtonCompare(this, TileInterface.COMPARE,
        			IComparer.COMPARE_FORESTRY | IntegrationForestry.Tag.GEN.getFlag() | IntegrationForestry.Tag.IS_ANALYZED.getFlag()));
        }
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/interface.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:interface.import"));
        drawString(7, 42, t("gui.refinedstorage:interface.export"));
        drawString(7, 122, t("container.inventory"));
    }
}
