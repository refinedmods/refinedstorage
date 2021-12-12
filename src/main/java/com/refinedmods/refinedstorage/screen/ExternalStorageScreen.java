package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.container.ExternalStorageContainer;
import com.refinedmods.refinedstorage.tile.ExternalStorageTile;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ExternalStorageScreen extends StorageScreen<ExternalStorageContainer> {
    public ExternalStorageScreen(ExternalStorageContainer container, Inventory inventory, Component title) {
        super(
            container,
            inventory,
            title,
            "gui/storage.png",
            new StorageScreenTileDataParameters(
                ExternalStorageTile.TYPE,
                NetworkNodeTile.REDSTONE_MODE,
                ExternalStorageTile.COMPARE,
                ExternalStorageTile.WHITELIST_BLACKLIST,
                ExternalStorageTile.PRIORITY,
                ExternalStorageTile.ACCESS_TYPE
            ),
            ExternalStorageTile.STORED::getValue,
            ExternalStorageTile.CAPACITY::getValue
        );
    }
}
