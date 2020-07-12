package com.refinedmods.refinedstorage.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.container.DiskDriveContainer;
import com.refinedmods.refinedstorage.tile.DiskDriveTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 79, 42, new TranslationTextComponent("gui.refinedstorage.disk_drive.disks").getString());

        super.renderForeground(matrixStack, mouseX, mouseY);
    }
}
