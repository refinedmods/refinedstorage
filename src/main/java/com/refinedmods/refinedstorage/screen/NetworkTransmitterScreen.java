package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.NetworkTransmitterContainer;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.NetworkTransmitterTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;

public class NetworkTransmitterScreen extends BaseScreen<NetworkTransmitterContainer> {
    public NetworkTransmitterScreen(NetworkTransmitterContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 176, 137, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/network_transmitter.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());

        String text;

        Optional<ResourceLocation> receiverDim = NetworkTransmitterTile.RECEIVER_DIMENSION.getValue();
        int distance = NetworkTransmitterTile.DISTANCE.getValue();

        if (!receiverDim.isPresent()) {
            text = I18n.get("gui.refinedstorage.network_transmitter.missing_card");
        } else if (distance != -1) {
            text = I18n.get("gui.refinedstorage.network_transmitter.distance", distance);
        } else {
            text = receiverDim.get().toString();
        }

        renderString(matrixStack, 51, 24, text);
        renderString(matrixStack, 7, 42, I18n.get("container.inventory"));
    }
}
