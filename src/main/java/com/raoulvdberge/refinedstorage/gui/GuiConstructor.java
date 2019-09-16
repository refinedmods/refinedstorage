package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.ContainerConstructor;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonCompare;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonConstuctorDrop;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.gui.widget.sidebutton.SideButtonType;
import com.raoulvdberge.refinedstorage.tile.TileConstructor;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiConstructor extends GuiBase<ContainerConstructor> {
    public GuiConstructor(ContainerConstructor container, PlayerInventory inventory) {
        super(container, 211, 137, inventory, null); // TODO TextComponent
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileConstructor.REDSTONE_MODE));

        addSideButton(new SideButtonType(this, TileConstructor.TYPE));

        addSideButton(new SideButtonCompare(this, TileConstructor.COMPARE, IComparer.COMPARE_NBT));
        addSideButton(new SideButtonConstuctorDrop(this));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/constructor.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:constructor"));
        renderString(7, 43, I18n.format("container.inventory"));
    }
}
