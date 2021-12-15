package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.DestructorContainerMenu;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*;
import com.refinedmods.refinedstorage.blockentity.DestructorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DestructorScreen extends BaseScreen<DestructorContainerMenu> {
    public DestructorScreen(DestructorContainerMenu containerMenu, Inventory playerInventory, Component title) {
        super(containerMenu, 211, 137, playerInventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));

        addSideButton(new TypeSideButton(this, DestructorBlockEntity.TYPE));

        addSideButton(new WhitelistBlacklistSideButton(this, DestructorBlockEntity.WHITELIST_BLACKLIST));

        addSideButton(new ExactModeSideButton(this, DestructorBlockEntity.COMPARE));

        addSideButton(new DestructorPickupSideButton(this));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/destructor.png");

        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY) {
        renderString(poseStack, 7, 7, title.getString());
        renderString(poseStack, 7, 43, I18n.get("container.inventory"));
    }
}
