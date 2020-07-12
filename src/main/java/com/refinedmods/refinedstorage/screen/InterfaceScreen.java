package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.InterfaceContainer;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.tile.InterfaceTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class InterfaceScreen extends BaseScreen<InterfaceContainer> {
    public InterfaceScreen(InterfaceContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 211, 217, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, InterfaceTile.REDSTONE_MODE));

        addSideButton(new ExactModeSideButton(this, InterfaceTile.COMPARE));
    }

    @Override
    public void tick(int x, int y) {
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/interface.png");

        blit(matrixStack, x, y, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, new TranslationTextComponent("gui.refinedstorage.interface.import").getString());
        renderString(matrixStack, 7, 42, new TranslationTextComponent("gui.refinedstorage.interface.export").getString());
        renderString(matrixStack, 7, 122, new TranslationTextComponent("container.inventory").getString());
    }
}
