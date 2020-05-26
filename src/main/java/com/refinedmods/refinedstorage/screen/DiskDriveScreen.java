package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.container.DiskDriveContainer;
import com.refinedmods.refinedstorage.tile.DiskDriveTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class DiskDriveScreen extends StorageScreen<DiskDriveContainer> {
    public DiskDriveScreen(DiskDriveContainer container, PlayerInventory inventory, ITextComponent title) {
        super(
            container,
            inventory,
            title,
            "gui/disk_drive.png",
            DiskDriveTile.TYPE,
            DiskDriveTile.REDSTONE_MODE,
            DiskDriveTile.COMPARE,
            DiskDriveTile.WHITELIST_BLACKLIST,
            DiskDriveTile.PRIORITY,
            DiskDriveTile.ACCESS_TYPE,
            DiskDriveTile.STORED::getValue,
            DiskDriveTile.CAPACITY::getValue
        );
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        renderString(79, 42, I18n.format("gui.refinedstorage.disk_drive.disks"));

        super.renderForeground(mouseX, mouseY);
    }
}
