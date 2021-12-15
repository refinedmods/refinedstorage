package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.ImporterContainerMenu;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.WhitelistBlacklistSideButton;
import com.refinedmods.refinedstorage.blockentity.ImporterBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ImporterScreen extends BaseScreen<ImporterContainerMenu> {
    public ImporterScreen(ImporterContainerMenu containerMenu, Inventory inventory, Component title) {
        super(containerMenu, 211, 137, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));

        addSideButton(new TypeSideButton(this, ImporterBlockEntity.TYPE));

        addSideButton(new WhitelistBlacklistSideButton(this, ImporterBlockEntity.WHITELIST_BLACKLIST));

        addSideButton(new ExactModeSideButton(this, ImporterBlockEntity.COMPARE));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RS.ID, "gui/importer.png");

        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY) {
        renderString(poseStack, 7, 7, title.getString());
        renderString(poseStack, 7, 43, I18n.get("container.inventory"));
    }
}
