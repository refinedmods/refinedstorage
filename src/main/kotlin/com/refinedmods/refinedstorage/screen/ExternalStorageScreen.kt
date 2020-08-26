package com.refinedmods.refinedstorage.screen

import com.refinedmods.refinedstorage.container.ExternalStorageContainer
import com.refinedmods.refinedstorage.tile.ExternalStorageTile
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text

class ExternalStorageScreen(container: ExternalStorageContainer, inventory: PlayerInventory?, title: Text?) : StorageScreen<ExternalStorageContainer?>(
        container,
        inventory,
        title,
        "gui/storage.png",
        ExternalStorageTile.TYPE,
        ExternalStorageTile.REDSTONE_MODE,
        ExternalStorageTile.COMPARE,
        ExternalStorageTile.WHITELIST_BLACKLIST,
        ExternalStorageTile.PRIORITY,
        ExternalStorageTile.ACCESS_TYPE,
        ExternalStorageTile.STORED::value,
        ExternalStorageTile.CAPACITY::value
)