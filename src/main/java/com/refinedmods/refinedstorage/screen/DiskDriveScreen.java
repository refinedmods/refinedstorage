package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.blockentity.DiskDriveBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.container.DiskDriveContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DiskDriveScreen extends StorageScreen<DiskDriveContainerMenu> {
    public DiskDriveScreen(DiskDriveContainerMenu containerMenu, Inventory inventory, Component title) {
        super(
            containerMenu,
            inventory,
            title,
            new ResourceLocation(RS.ID, "textures/gui/disk_drive.png"),
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
    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        renderString(graphics, 79, 42, I18n.get("gui.refinedstorage.disk_drive.disks"));
        super.renderForeground(graphics, mouseX, mouseY);
    }
}
