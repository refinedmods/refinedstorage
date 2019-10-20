package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.RelayContainer;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.raoulvdberge.refinedstorage.tile.RelayTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class RelayScreen extends BaseScreen<RelayContainer> {
    public RelayScreen(RelayContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 176, 131, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, RelayTile.REDSTONE_MODE));
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
        renderString(7, 7, title.getFormattedText());
        renderString(7, 39, I18n.format("container.inventory"));
    }
}
