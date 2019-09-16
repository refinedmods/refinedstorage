package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerInterface;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonCompare;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.TileInterface;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiInterface extends GuiBase<ContainerInterface> {
    public GuiInterface(ContainerInterface container, PlayerInventory inventory) {
        super(container, 211, 217, inventory, null);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileInterface.REDSTONE_MODE));

        addSideButton(new SideButtonCompare(this, TileInterface.COMPARE, IComparer.COMPARE_NBT));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/interface.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:interface.import"));
        renderString(7, 42, I18n.format("gui.refinedstorage:interface.export"));
        renderString(7, 122, I18n.format("container.inventory"));
    }
}
