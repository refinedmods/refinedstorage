package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.container.ExternalStorageContainer;
import com.refinedmods.refinedstorage.tile.ExternalStorageTile;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ExternalStorageScreen extends StorageScreen<ExternalStorageContainer> {
    public ExternalStorageScreen(ExternalStorageContainer container, PlayerInventory inventory, ITextComponent title) {
        super(
            container,
            inventory,
            title,
            "gui/storage.png",
            ExternalStorageTile.TYPE,
            NetworkNodeTile.REDSTONE_MODE,
            ExternalStorageTile.COMPARE,
            ExternalStorageTile.WHITELIST_BLACKLIST,
            ExternalStorageTile.PRIORITY,
            ExternalStorageTile.ACCESS_TYPE,
            ExternalStorageTile.STORED::getValue,
            ExternalStorageTile.CAPACITY::getValue
        );
    }
}
