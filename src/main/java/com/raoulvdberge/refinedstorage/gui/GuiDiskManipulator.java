package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerDiskManipulator;
import com.raoulvdberge.refinedstorage.gui.control.*;
import com.raoulvdberge.refinedstorage.tile.TileDiskManipulator;
import net.minecraft.entity.player.PlayerInventory;

public class GuiDiskManipulator extends GuiBase<ContainerDiskManipulator> {
    public GuiDiskManipulator(ContainerDiskManipulator container, PlayerInventory playerInventory) {
        super(container, 211, 211, playerInventory, null);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileDiskManipulator.REDSTONE_MODE));
        addSideButton(new SideButtonIOMode(this));
        addSideButton(new SideButtonType(this, TileDiskManipulator.TYPE));
        addSideButton(new SideButtonMode(this, TileDiskManipulator.MODE));
        addSideButton(new SideButtonCompare(this, TileDiskManipulator.COMPARE, IComparer.COMPARE_NBT));
    }

    @Override
    public void update(int x, int y) {

    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/disk_manipulator.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("block.refinedstorage:disk_manipulator.name"));
        drawString(7, 117, t("container.inventory"));
        drawString(43, 45, t("gui.refinedstorage:fluid_interface.in"));
        drawString(115, 45, t("gui.refinedstorage:fluid_interface.out"));
    }
}
