package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkTransmitterBlockEntity;
import com.refinedmods.refinedstorage.container.NetworkTransmitterContainerMenu;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class NetworkTransmitterScreen extends BaseScreen<NetworkTransmitterContainerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RS.ID, "textures/gui/network_transmitter.png");

    public NetworkTransmitterScreen(NetworkTransmitterContainerMenu containerMenu, Inventory inventory, Component title) {
        super(containerMenu, 176, 137, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        renderString(graphics, 7, 7, title.getString());

        String text;

        Optional<ResourceLocation> receiverDim = NetworkTransmitterBlockEntity.RECEIVER_DIMENSION.getValue();
        int distance = NetworkTransmitterBlockEntity.DISTANCE.getValue();

        if (!receiverDim.isPresent()) {
            text = I18n.get("gui.refinedstorage.network_transmitter.missing_card");
        } else if (distance != -1) {
            text = I18n.get("gui.refinedstorage.network_transmitter.distance", distance);
        } else {
            text = receiverDim.get().toString();
        }

        renderString(graphics, 51, 24, text);
        renderString(graphics, 7, 42, I18n.get("container.inventory"));
    }
}
