package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.DestructorContainer;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.refinedmods.refinedstorage.tile.DestructorTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DestructorScreen extends BaseScreen<DestructorContainer> {
    public DestructorScreen(DestructorContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, 211, 137, playerInventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, DestructorTile.REDSTONE_MODE));

        addSideButton(new TypeSideButton(this, DestructorTile.TYPE));

        addSideButton(new WhitelistBlacklistSideButton(this, DestructorTile.WHITELIST_BLACKLIST));

        addSideButton(new ExactModeSideButton(this, DestructorTile.COMPARE));

        addSideButton(new DestructorPickupSideButton(this));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/destructor.png");

        blit(matrixStack, x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.toString());
        renderString(matrixStack, 7, 43, new TranslationTextComponent("container.inventory").getString());
    }
}
