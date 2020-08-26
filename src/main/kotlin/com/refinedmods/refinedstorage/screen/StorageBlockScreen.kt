package com.refinedmods.refinedstorage.screen

import com.refinedmods.refinedstorage.container.StorageContainer
import com.refinedmods.refinedstorage.tile.StorageTile
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text
import java.util.function.Supplier

class StorageBlockScreen(container: StorageContainer, inventory: PlayerInventory?, title: Text?) : StorageScreen<StorageContainer?>(
        container,
        inventory,
        title,
        "gui/storage.png",
        null,
        StorageTile.REDSTONE_MODE,
        StorageTile.COMPARE,
        StorageTile.WHITELIST_BLACKLIST,
        StorageTile.PRIORITY,
        StorageTile.ACCESS_TYPE,
        StorageTile.STORED::value,
        Supplier { (container.tile as StorageTile?)!!.itemStorageType.getCapacity() as Long }
)