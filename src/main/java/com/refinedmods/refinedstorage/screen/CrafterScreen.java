package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.CrafterContainer;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class CrafterScreen extends BaseScreen<CrafterContainer> {
    public CrafterScreen(CrafterContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 211, 137, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        // NO OP
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/crafter.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, RenderUtils.shorten(title.getString(), 26));
        renderString(matrixStack, 7, 43, I18n.get("container.inventory"));
    }
}
