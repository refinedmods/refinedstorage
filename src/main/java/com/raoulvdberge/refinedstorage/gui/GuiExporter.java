package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerExporter;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonCompare;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonType;
import com.raoulvdberge.refinedstorage.tile.TileExporter;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiExporter extends GuiBase<ContainerExporter> {
    public GuiExporter(ContainerExporter container, PlayerInventory playerInventory) {
        super(container, 211, 137, playerInventory, null);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileExporter.REDSTONE_MODE));

        addSideButton(new SideButtonType(this, TileExporter.TYPE));

        addSideButton(new SideButtonCompare(this, TileExporter.COMPARE, IComparer.COMPARE_NBT));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/exporter.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:exporter"));
        renderString(7, 43, I18n.format("container.inventory"));
    }
}
