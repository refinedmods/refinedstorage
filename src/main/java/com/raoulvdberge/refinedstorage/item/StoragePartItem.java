package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.storage.ItemStorageType;
import net.minecraft.item.Item;

public class StoragePartItem extends Item {
    public StoragePartItem(ItemStorageType type) {
        super(new Item.Properties().group(RS.MAIN_GROUP));

        this.setRegistryName(RS.ID, type.getName() + "_storage_part");
    }

    public static StoragePartItem getByType(ItemStorageType type) {
        switch (type) {
            case ONE_K:
                return RSItems.ONE_K_STORAGE_PART;
            case FOUR_K:
                return RSItems.FOUR_K_STORAGE_PART;
            case SIXTEEN_K:
                return RSItems.SIXTEEN_K_STORAGE_PART;
            case SIXTY_FOUR_K:
                return RSItems.SIXTY_FOUR_K_STORAGE_PART;
            default:
                throw new IllegalArgumentException("Cannot get storage part of " + type);
        }
    }
}
