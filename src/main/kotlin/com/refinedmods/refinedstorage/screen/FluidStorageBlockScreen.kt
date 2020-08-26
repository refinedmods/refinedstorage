package com.refinedmods.refinedstorage.screen

import com.refinedmods.refinedstorage.container.FluidStorageContainer
import com.refinedmods.refinedstorage.tile.FluidStorageTile
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text
import java.util.function.Supplier

class FluidStorageBlockScreen(container: FluidStorageContainer, inventory: PlayerInventory?, title: Text?) : StorageScreen<FluidStorageContainer?>(
        container,
        inventory,
        title,
        "gui/storage.png",
        null,
        FluidStorageTile.REDSTONE_MODE,
        FluidStorageTile.COMPARE,
        FluidStorageTile.WHITELIST_BLACKLIST,
        FluidStorageTile.PRIORITY,
        FluidStorageTile.ACCESS_TYPE,
        FluidStorageTile.STORED::value,
        Supplier { (container.tile as FluidStorageTile?)!!.fluidStorageType.getCapacity() as Long }
)