package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.container.ExternalStorageContainer;
import com.refinedmods.refinedstorage.blockentity.ExternalStorageBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ExternalStorageScreen extends StorageScreen<ExternalStorageContainer> {
    public ExternalStorageScreen(ExternalStorageContainer container, Inventory inventory, Component title) {
        super(
            container,
            inventory,
            title,
            "gui/storage.png",
            new StorageScreenSynchronizationParameters(
                ExternalStorageBlockEntity.TYPE,
                NetworkNodeBlockEntity.REDSTONE_MODE,
                ExternalStorageBlockEntity.COMPARE,
                ExternalStorageBlockEntity.WHITELIST_BLACKLIST,
                ExternalStorageBlockEntity.PRIORITY,
                ExternalStorageBlockEntity.ACCESS_TYPE
            ),
            ExternalStorageBlockEntity.STORED::getValue,
            ExternalStorageBlockEntity.CAPACITY::getValue
        );
    }
}
