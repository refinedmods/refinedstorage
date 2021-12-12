package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
import net.minecraft.item.Item;

public class StoragePartItem extends Item {
    public StoragePartItem() {
        super(new Item.Properties().tab(RS.MAIN_GROUP));
    }

    public static StoragePartItem getByType(ItemStorageType type) {
        return RSItems.ITEM_STORAGE_PARTS.get(type).get();
    }
}
