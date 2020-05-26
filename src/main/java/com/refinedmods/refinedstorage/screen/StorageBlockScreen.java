package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.container.StorageContainer;
import com.refinedmods.refinedstorage.tile.StorageTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class StorageBlockScreen extends StorageScreen<StorageContainer> {
    public StorageBlockScreen(StorageContainer container, PlayerInventory inventory, ITextComponent title) {
        super(
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
            StorageTile.STORED::getValue,
            () -> (long) ((StorageTile) container.getTile()).getItemStorageType().getCapacity()
        );
    }
}
