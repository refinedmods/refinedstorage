package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.RelayContainer;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.SideButtonRedstoneMode;
import com.raoulvdberge.refinedstorage.tile.TileRelay;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiRelay extends BaseScreen<RelayContainer> {
    public GuiRelay(RelayContainer container, PlayerInventory inventory) {
        super(container, 176, 131, inventory, null);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileRelay.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/relay.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:relay"));
        renderString(7, 39, I18n.format("container.inventory"));
    }
}
