package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.InterfaceContainer;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.tile.InterfaceTile;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class InterfaceScreen extends BaseScreen<InterfaceContainer> {
    public InterfaceScreen(InterfaceContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 211, 217, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE));

        addSideButton(new ExactModeSideButton(this, InterfaceTile.COMPARE));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/interface.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, I18n.get("gui.refinedstorage.interface.import"));
        renderString(matrixStack, 7, 42, I18n.get("gui.refinedstorage.interface.export"));
        renderString(matrixStack, 7, 122, I18n.get("container.inventory"));
    }
}
