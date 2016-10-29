package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.raoulvdberge.refinedstorage.api.storage.item.IItemStorage;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class ItemStorageExternal implements IItemStorage {
    private List<ItemStack> cache;

    public abstract int getCapacity();

    public boolean updateCache() {
        List<ItemStack> newStacks = getStacks();

        if (this.cache == null) {
            this.cache = newStacks;
        } else if (newStacks.size() != cache.size()) {
            this.cache = newStacks;

            return true;
        } else {
            for (int i = 0; i < newStacks.size(); ++i) {
                if (!API.instance().getComparer().isEqual(newStacks.get(i), cache.get(i))) {
                    this.cache = newStacks;

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
