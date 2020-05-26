package com.refinedmods.refinedstorage.item;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType;
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
