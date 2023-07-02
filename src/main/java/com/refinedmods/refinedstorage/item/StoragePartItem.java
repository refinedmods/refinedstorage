package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import net.minecraft.world.item.Item;

public class StoragePartItem extends Item {
    public StoragePartItem() {
        super(new Item.Properties());
    }

    public static StoragePartItem getByType(ItemStorageType type) {
        return RSItems.ITEM_STORAGE_PARTS.get(type).get();
    }
}
