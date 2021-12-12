package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.DestructorContainer;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.refinedmods.refinedstorage.tile.DestructorTile;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class DestructorScreen extends BaseScreen<DestructorContainer> {
    public DestructorScreen(DestructorContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, 211, 137, playerInventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE));

        addSideButton(new TypeSideButton(this, DestructorTile.TYPE));

        addSideButton(new WhitelistBlacklistSideButton(this, DestructorTile.WHITELIST_BLACKLIST));

        addSideButton(new ExactModeSideButton(this, DestructorTile.COMPARE));

        addSideButton(new DestructorPickupSideButton(this));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/destructor.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, 43, I18n.get("container.inventory"));
    }
}
