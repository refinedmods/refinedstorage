package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.container.StorageContainerMenu;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.StorageBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class StorageBlockScreen extends StorageScreen<StorageContainerMenu> {
    public StorageBlockScreen(StorageContainerMenu containerMenu, Inventory inventory, Component title) {
        super(
            containerMenu,
            inventory,
            title,
            "gui/storage.png",
            new StorageScreenSynchronizationParameters(
                null,
                NetworkNodeBlockEntity.REDSTONE_MODE,
                StorageBlockEntity.COMPARE,
                StorageBlockEntity.WHITELIST_BLACKLIST,
                StorageBlockEntity.PRIORITY,
                StorageBlockEntity.ACCESS_TYPE
            ),
            StorageBlockEntity.STORED::getValue,
            () -> (long) ((StorageBlockEntity) containerMenu.getBlockEntity()).getItemStorageType().getCapacity()
        );
    }
}
