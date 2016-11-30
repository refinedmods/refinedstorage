package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.item.IItemStorage;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemStorageExternal implements IItemStorage {
    private List<ItemStack> cache;

    public abstract int getCapacity();

    public void detectChanges(INetworkMaster network) {
        // if we are insert-only, we don't care about sending changes
        if (getAccessType() == AccessType.INSERT) {
            return;
        }

        if (cache == null) {
            cache = getStacks();

            return;
        }

        List<ItemStack> newStacks = getStacks();

        for (int i = 0; i < newStacks.size(); ++i) {
            ItemStack actual = newStacks.get(i);

            // If we exceed the cache size, than that means this items is added
            if (i >= cache.size()) {
                if (actual != null) {
                    network.getItemStorageCache().add(actual, actual.getCount(), false);
                }

                continue;
            }

            ItemStack cached = cache.get(i);

            if (!cached.isEmpty() && actual.isEmpty()) {
                // If the cached is not empty but the actual is, we remove this item
                network.getItemStorageCache().remove(cached, cached.getCount());
            } else if (cached.isEmpty() && !actual.isEmpty()) {
                // If the cached is empty and the actual isn't, we added this item
                network.getItemStorageCache().add(actual, actual.getCount(), false);
            } else if (cached.isEmpty() && actual.isEmpty()) {
                // If they're both empty, nothing happens
            } else if (!API.instance().getComparer().isEqualNoQuantity(cached, actual)) {
                // If both items mismatch, remove the old and add the new
                network.getItemStorageCache().remove(cached, cached.getCount());
                network.getItemStorageCache().add(actual, actual.getCount(), false);
            } else if (cached.getCount() != actual.getCount()) {
                int delta = actual.getCount() - cached.getCount();

                if (delta > 0) {
                    network.getItemStorageCache().add(actual, delta, false);
                } else {
                    network.getItemStorageCache().remove(actual, Math.abs(delta));
                }
            }
        }

        // If the cache size is somehow bigger than the actual stacks, that means the inventory shrunk
        // In that case, we remove the items that have been removed due to the shrinkage
        if (cache.size() > newStacks.size()) {
            for (int i = newStacks.size(); i < cache.size(); ++i) {
                if (cache.get(i) != ItemStack.EMPTY) {
                    network.getItemStorageCache().remove(cache.get(i), cache.get(i).getCount());
                }
            }
        }

        this.cache = newStacks;
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable ItemStack remainder) {
        return remainder == null ? size : (size - remainder.getCount());
    }
}
