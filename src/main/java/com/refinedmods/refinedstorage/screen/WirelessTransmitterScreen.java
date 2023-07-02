package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.WirelessTransmitterBlockEntity;
import com.refinedmods.refinedstorage.container.WirelessTransmitterContainerMenu;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WirelessTransmitterScreen extends BaseScreen<WirelessTransmitterContainerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(RS.ID, "textures/gui/wireless_transmitter.png");

    public WirelessTransmitterScreen(WirelessTransmitterContainerMenu containerMenu, Inventory inventory, Component title) {
        super(containerMenu, 211, 137, inventory, title);
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
        renderString(graphics, 28, 25, I18n.get("gui.refinedstorage.wireless_transmitter.distance", WirelessTransmitterBlockEntity.RANGE.getValue()));
        renderString(graphics, 7, 43, I18n.get("container.inventory"));
    }
}
