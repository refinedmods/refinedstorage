package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.container.DiskDriveContainer
import com.refinedmods.refinedstorage.tile.DiskDriveTile
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text

class DiskDriveScreen(container: DiskDriveContainer, inventory: PlayerInventory?, title: Text?) : StorageScreen<DiskDriveContainer?>(
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
        DiskDriveTile.STORED::value,
        DiskDriveTile.CAPACITY::value
) {
    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 79, 42, I18n.format("gui.refinedstorage.disk_drive.disks"))
        super.renderForeground(matrixStack, mouseX, mouseY)
    }
}