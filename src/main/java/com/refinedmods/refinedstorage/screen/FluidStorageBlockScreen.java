package com.refinedmods.refinedstorage.screen;

import com.refinedmods.refinedstorage.container.FluidStorageContainer;
import com.refinedmods.refinedstorage.tile.FluidStorageTile;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class FluidStorageBlockScreen extends StorageScreen<FluidStorageContainer> {
    public FluidStorageBlockScreen(FluidStorageContainer container, PlayerInventory inventory, ITextComponent title) {
        super(
            container,
            inventory,
            title,
            "gui/storage.png",
            new StorageScreenTileDataParameters(
                null,
                NetworkNodeTile.REDSTONE_MODE,
                FluidStorageTile.COMPARE,
                FluidStorageTile.WHITELIST_BLACKLIST,
                FluidStorageTile.PRIORITY,
                FluidStorageTile.ACCESS_TYPE
            ),
            FluidStorageTile.STORED::getValue,
            () -> (long) ((FluidStorageTile) container.getTile()).getFluidStorageType().getCapacity()
        );
    }
}
