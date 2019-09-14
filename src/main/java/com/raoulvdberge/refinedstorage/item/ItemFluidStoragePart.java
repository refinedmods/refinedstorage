package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.storage.FluidStorageType;
import net.minecraft.item.Item;

public class ItemFluidStoragePart extends Item {
    public ItemFluidStoragePart(FluidStorageType type) {
        super(new Item.Properties().group(RS.MAIN_GROUP));

        this.setRegistryName(RS.ID, type.getName() + "_fluid_storage_part");
    }
}
