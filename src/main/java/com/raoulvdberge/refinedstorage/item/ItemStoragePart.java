package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.storage.ItemStorageType;
import net.minecraft.item.Item;

public class ItemStoragePart extends ItemBase {
    public ItemStoragePart(ItemStorageType type) {
        super(new Item.Properties().group(RS.MAIN_GROUP));

        this.setRegistryName(RS.ID, type.getName() + "_storage_part");
    }
}
