package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.container.FluidStorageContainer;
import com.refinedmods.refinedstorage.blockentity.FluidStorageBlockEntity;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FluidStorageBlockScreen extends StorageScreen<FluidStorageContainer> {
    public FluidStorageBlockScreen(FluidStorageContainer container, Inventory inventory, Component title) {
        super(
            container,
            inventory,
            title,
            "gui/storage.png",
            new StorageScreenSynchronizationParameters(
                null,
                NetworkNodeBlockEntity.REDSTONE_MODE,
                FluidStorageBlockEntity.COMPARE,
                FluidStorageBlockEntity.WHITELIST_BLACKLIST,
                FluidStorageBlockEntity.PRIORITY,
                FluidStorageBlockEntity.ACCESS_TYPE
            ),
            FluidStorageBlockEntity.STORED::getValue,
            () -> (long) ((FluidStorageBlockEntity) container.getBlockEntity()).getFluidStorageType().getCapacity()
        );
    }
}
