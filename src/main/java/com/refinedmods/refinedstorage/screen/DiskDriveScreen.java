package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.container.DiskDriveContainer;
import com.refinedmods.refinedstorage.blockentity.DiskDriveBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DiskDriveScreen extends StorageScreen<DiskDriveContainer> {
    public DiskDriveScreen(DiskDriveContainer container, Inventory inventory, Component title) {
        super(
            container,
            inventory,
            title,
            "gui/disk_drive.png",
            new StorageScreenSynchronizationParameters(
                DiskDriveBlockEntity.TYPE,
                NetworkNodeBlockEntity.REDSTONE_MODE,
                DiskDriveBlockEntity.COMPARE,
                DiskDriveBlockEntity.WHITELIST_BLACKLIST,
                DiskDriveBlockEntity.PRIORITY,
                DiskDriveBlockEntity.ACCESS_TYPE
            ),
            DiskDriveBlockEntity.STORED::getValue,
            DiskDriveBlockEntity.CAPACITY::getValue
        );
    }

    @Override
    public void renderForeground(PoseStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 79, 42, I18n.get("gui.refinedstorage.disk_drive.disks"));

        super.renderForeground(matrixStack, mouseX, mouseY);
    }
}
