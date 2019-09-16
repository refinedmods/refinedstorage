package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.apiimpl.storage.FluidStorageType;
import net.minecraft.item.Item;

public class FluidStoragePartItem extends Item {
    public FluidStoragePartItem(FluidStorageType type) {
        super(new Item.Properties().group(RS.MAIN_GROUP));

        this.setRegistryName(RS.ID, type.getName() + "_fluid_storage_part");
    }

    public static FluidStoragePartItem getByType(FluidStorageType type) {
        switch (type) {
            case SIXTY_FOUR_K:
                return RSItems.SIXTY_FOUR_K_FLUID_STORAGE_PART;
            case TWO_HUNDRED_FIFTY_SIX_K:
                return RSItems.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_PART;
            case THOUSAND_TWENTY_FOUR_K:
                return RSItems.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_PART;
            case FOUR_THOUSAND_NINETY_SIX_K:
                return RSItems.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_PART;
            default:
                throw new IllegalArgumentException("Cannot get fluid storage part of " + type);
        }
    }
}
