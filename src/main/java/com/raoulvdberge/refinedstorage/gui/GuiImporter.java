package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerImporter;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonCompare;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonMode;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonType;
import com.raoulvdberge.refinedstorage.tile.TileImporter;
import net.minecraft.entity.player.PlayerInventory;

public class GuiImporter extends GuiBase<ContainerImporter> {
    public GuiImporter(ContainerImporter container, PlayerInventory inventory) {
        super(container, 211, 137, inventory, null);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileImporter.REDSTONE_MODE));

        addSideButton(new SideButtonType(this, TileImporter.TYPE));

        addSideButton(new SideButtonMode(this, TileImporter.MODE));

        addSideButton(new SideButtonCompare(this, TileImporter.COMPARE, IComparer.COMPARE_NBT));
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
