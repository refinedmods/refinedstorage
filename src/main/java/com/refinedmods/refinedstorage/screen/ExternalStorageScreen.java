package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.container.ExternalStorageContainerMenu;
import com.refinedmods.refinedstorage.blockentity.ExternalStorageBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ExternalStorageScreen extends StorageScreen<ExternalStorageContainerMenu> {
    public ExternalStorageScreen(ExternalStorageContainerMenu containerMenu, Inventory inventory, Component title) {
        super(
            containerMenu,
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
