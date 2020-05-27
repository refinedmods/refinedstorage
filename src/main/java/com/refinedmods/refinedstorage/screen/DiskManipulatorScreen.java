package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.DiskManipulatorContainer;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.refinedmods.refinedstorage.tile.DiskManipulatorTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class DiskManipulatorScreen extends BaseScreen<DiskManipulatorContainer> {
    public DiskManipulatorScreen(DiskManipulatorContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, 211, 211, playerInventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, DiskManipulatorTile.REDSTONE_MODE));
        addSideButton(new IoModeSideButton(this));
        addSideButton(new TypeSideButton(this, DiskManipulatorTile.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, DiskManipulatorTile.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, DiskManipulatorTile.COMPARE));
    }

    @Override
    public void tick(int x, int y) {

    }

    @Override
    public void renderBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/disk_manipulator.png");

        blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(7, 7, title.getFormattedText());
        renderString(7, 117, I18n.format("container.inventory"));
        renderString(43, 45, I18n.format("gui.refinedstorage.disk_manipulator.in"));
        renderString(115, 45, I18n.format("gui.refinedstorage.disk_manipulator.out"));
    }
}
