package com.raoulvdberge.refinedstorage.screen;

import com.raoulvdberge.refinedstorage.container.FluidStorageContainer;
import com.raoulvdberge.refinedstorage.tile.FluidStorageTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class FluidStorageBlockScreen extends StorageScreen<FluidStorageContainer> {
    public FluidStorageBlockScreen(FluidStorageContainer container, PlayerInventory inventory, ITextComponent title) {
        super(
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
            FluidStorageTile.STORED::getValue,
            () -> (long) ((FluidStorageTile) container.getTile()).getFluidStorageType().getCapacity()
        );
    }
}
