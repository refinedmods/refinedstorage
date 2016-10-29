package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.raoulvdberge.refinedstorage.api.storage.item.IItemStorage;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class ItemStorageExternal implements IItemStorage {
    private List<ItemStack> cache;

    public abstract int getCapacity();

    public boolean updateCache() {
        List<ItemStack> items = getStacks();

        if (this.cache == null) {
            this.cache = items;
        } else if (items.size() != cache.size()) {
            this.cache = items;

            return true;
        } else {
            for (int i = 0; i < items.size(); ++i) {
                if (!API.instance().getComparer().isEqual(items.get(i), cache.get(i))) {
                    this.cache = items;

                    return true;
                }
            }
        }

        return false;
    }

    public void updateCacheForcefully() {
        this.cache = getStacks();
    }
}
