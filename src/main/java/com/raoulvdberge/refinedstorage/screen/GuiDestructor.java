package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.container.DestructorContainer;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.*;
import com.raoulvdberge.refinedstorage.tile.TileDestructor;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiDestructor extends BaseScreen<DestructorContainer> {
    public GuiDestructor(DestructorContainer container, PlayerInventory playerInventory) {
        super(container, 211, 137, playerInventory, null);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileDestructor.REDSTONE_MODE));

        addSideButton(new SideButtonType(this, TileDestructor.TYPE));

        addSideButton(new SideButtonMode(this, TileDestructor.MODE));

        addSideButton(new SideButtonCompare(this, TileDestructor.COMPARE, IComparer.COMPARE_NBT));

        addSideButton(new SideButtonDestructorPickup(this));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/destructor.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:destructor"));
        renderString(7, 43, I18n.format("container.inventory"));
    }
}
