package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ImporterContainer;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.raoulvdberge.refinedstorage.screen.widget.sidebutton.WhitelistBlacklistSideButton;
import com.raoulvdberge.refinedstorage.tile.TileImporter;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;

public class GuiImporter extends BaseScreen<ImporterContainer> {
    public GuiImporter(ImporterContainer container, PlayerInventory inventory) {
        super(container, 211, 137, inventory, null);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, TileImporter.REDSTONE_MODE));

        addSideButton(new TypeSideButton(this, TileImporter.TYPE));

        addSideButton(new WhitelistBlacklistSideButton(this, TileImporter.WHITELIST_BLACKLIST));

        addSideButton(new ExactModeSideButton(this, TileImporter.COMPARE));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/importer.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, I18n.format("gui.refinedstorage:importer"));
        renderString(7, 43, I18n.format("container.inventory"));
    }
}
