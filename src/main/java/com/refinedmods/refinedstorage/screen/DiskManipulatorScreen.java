package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.DiskManipulatorContainerMenu;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.refinedmods.refinedstorage.blockentity.DiskManipulatorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DiskManipulatorScreen extends BaseScreen<DiskManipulatorContainerMenu> {
    public DiskManipulatorScreen(DiskManipulatorContainerMenu containerMenu, Inventory playerInventory, Component title) {
        super(containerMenu, 211, 211, playerInventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));
        addSideButton(new IoModeSideButton(this));
        addSideButton(new TypeSideButton(this, DiskManipulatorBlockEntity.TYPE));
        addSideButton(new WhitelistBlacklistSideButton(this, DiskManipulatorBlockEntity.WHITELIST_BLACKLIST));
        addSideButton(new ExactModeSideButton(this, DiskManipulatorBlockEntity.COMPARE));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/disk_manipulator.png");

        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY) {
        renderString(poseStack, 7, 7, title.getString());
        renderString(poseStack, 7, 117, I18n.get("container.inventory"));
        renderString(poseStack, 43, 45, I18n.get("gui.refinedstorage.disk_manipulator.in"));
        renderString(poseStack, 115, 45, I18n.get("gui.refinedstorage.disk_manipulator.out"));
    }
}
