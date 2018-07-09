package com.raoulvdberge.refinedstorage.item.info;

import net.minecraft.util.ResourceLocation;

public class ItemInfo implements IItemInfo {
    private final ResourceLocation id;

    public ItemInfo(String modId, String id) {
        this.id = new ResourceLocation(modId, id);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
}
