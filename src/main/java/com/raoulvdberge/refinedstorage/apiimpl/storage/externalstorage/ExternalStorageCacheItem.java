package com.raoulvdberge.refinedstorage.apiimpl.storage.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ExternalStorageCacheItem {
    private List<ItemStack> cache;

    public void update(INetwork network, @Nullable IItemHandler handler) {
        if (handler == null) {
            return;
        }

        if (cache == null) {
            cache = new ArrayList<>();

            for (int i = 0; i < handler.getSlots(); ++i) {
                cache.add(handler.getStackInSlot(i).copy());
            }

            return;
        }

        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack actual = handler.getStackInSlot(i);

            if (i >= cache.size()) { // ENLARGED
                if (!actual.isEmpty()) {
                    network.getItemStorageCache().add(actual, actual.getCount(), false, true);

                    cache.add(actual.copy());
                }

                continue;
            }

            ItemStack cached = cache.get(i);

            if (!cached.isEmpty() && actual.isEmpty()) { // REMOVED
                network.getItemStorageCache().remove(cached, cached.getCount(), true);

                cache.set(i, ItemStack.EMPTY);
            } else if (cached.isEmpty() && !actual.isEmpty()) { // ADDED
                network.getItemStorageCache().add(actual, actual.getCount(), false, true);

                cache.set(i, actual.copy());
            } else if (!API.instance().getComparer().isEqualNoQuantity(cached, actual)) { // CHANGED
                network.getItemStorageCache().remove(cached, cached.getCount(), true);
                network.getItemStorageCache().add(actual, actual.getCount(), false, true);

                cache.set(i, actual.copy());
            } else if (cached.getCount() != actual.getCount()) { // COUNT_CHANGED
                int delta = actual.getCount() - cached.getCount();

                if (delta > 0) {
                    network.getItemStorageCache().add(actual, delta, false, true);

                    cached.grow(delta);
                } else {
                    network.getItemStorageCache().remove(actual, Math.abs(delta), true);

                    cached.shrink(Math.abs(delta));
                }
            }
        }

        if (cache.size() > handler.getSlots()) { // SHRUNK
            for (int i = cache.size() - 1; i >= handler.getSlots(); --i) { // Reverse order for the remove call.
                ItemStack cached = cache.get(i);

                if (!cached.isEmpty()) {
                    network.getItemStorageCache().remove(cached, cached.getCount(), true);
                }

                cache.remove(i);
            }
        }

        network.getItemStorageCache().flush();
    }
}
