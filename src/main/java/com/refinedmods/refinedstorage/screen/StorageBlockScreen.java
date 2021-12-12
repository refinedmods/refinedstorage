package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.container.StorageContainer;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.StorageTile;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class StorageBlockScreen extends StorageScreen<StorageContainer> {
    public StorageBlockScreen(StorageContainer container, Inventory inventory, Component title) {
        super(
            container,
            inventory,
            title,
            "gui/storage.png",
            new StorageScreenTileDataParameters(
                null,
                NetworkNodeTile.REDSTONE_MODE,
                StorageTile.COMPARE,
                StorageTile.WHITELIST_BLACKLIST,
                StorageTile.PRIORITY,
                StorageTile.ACCESS_TYPE
            ),
            StorageTile.STORED::getValue,
            () -> (long) ((StorageTile) container.getTile()).getItemStorageType().getCapacity()
        );
    }
}
