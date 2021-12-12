package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType;
import net.minecraft.world.item.Item;

public class FluidStoragePartItem extends Item {
    public FluidStoragePartItem() {
        super(new Item.Properties().tab(RS.MAIN_GROUP));
    }

    public static FluidStoragePartItem getByType(FluidStorageType type) {
        return RSItems.FLUID_STORAGE_PARTS.get(type).get();
    }
}
