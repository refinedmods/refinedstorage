package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.DiskManipulatorContainer;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.refinedmods.refinedstorage.tile.DiskManipulatorTile;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class DiskManipulatorScreen extends BaseScreen<DiskManipulatorContainer> {
    public DiskManipulatorScreen(DiskManipulatorContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, 211, 211, playerInventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE));
        addSideButton(new IoModeSideButton(this));
        addSideButton(new TypeSideButton(this, DiskManipulatorTile.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, DiskManipulatorTile.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, DiskManipulatorTile.COMPARE));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/disk_manipulator.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 117, I18n.get("container.inventory"));
        renderString(matrixStack, 43, 45, I18n.get("gui.refinedstorage.disk_manipulator.in"));
        renderString(matrixStack, 115, 45, I18n.get("gui.refinedstorage.disk_manipulator.out"));
    }
}
