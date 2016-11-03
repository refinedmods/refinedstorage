package com.raoulvdberge.refinedstorage.tile.externalstorage;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.storage.item.IItemStorage;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.LinkedList;
import java.util.List;

public abstract class ItemStorageExternal implements IItemStorage {
    private List<ItemStack> cache;

    public abstract int getCapacity();

    public void detectChanges(INetworkMaster network) {
        if (cache == null) {
            updateForced();
        } else {
            LinkedList<ItemStack> changes = new LinkedList<>();

            List<ItemStack> newStacks = getStacks();

            for (int i = 0; i < newStacks.size(); ++i) {
                ItemStack actual = newStacks.get(i);

                // If we exceed the cache size, than that means this items is added
                if (i >= cache.size()) {
                    if (actual != null) {
                        changes.add(ItemHandlerHelper.copyStackWithSize(actual, actual.stackSize));
                    }

                    continue;
                }

                ItemStack cached = cache.get(i);

                if (cached != null && actual == null) {
                    // If the cached is not null but the actual is, we removed this item
                    changes.add(ItemHandlerHelper.copyStackWithSize(cached, -cached.stackSize));
                } else if (cached == null && actual != null) {
                    // If the cached is null and the actual isn't, we added this item
                    changes.add(ItemHandlerHelper.copyStackWithSize(actual, actual.stackSize));
                } else if (cached == null && actual == null) {
                    // If they're both null, nothing happens
                } else if (!API.instance().getComparer().isEqualNoQuantity(cached, actual)) {
                    // If both items mismatch, remove the old and add the new
                    changes.add(ItemHandlerHelper.copyStackWithSize(cached, -cached.stackSize));
                    changes.add(ItemHandlerHelper.copyStackWithSize(actual, actual.stackSize));
                } else if (cached.stackSize != actual.stackSize) {
                    // If both items mismatch on itemcount, apply the change
                    changes.add(ItemHandlerHelper.copyStackWithSize(cached, actual.stackSize - cached.stackSize));
                }
            }

            // If the cache size is somehow bigger than the actual stacks, that means the inventory shrunk
            // In that case, we remove the items that have been removed due to the shrinkage
            if (cache.size() > newStacks.size()) {
                for (int i = newStacks.size(); i < cache.size(); ++i) {
                    ItemStack change = ItemHandlerHelper.copyStackWithSize(cache.get(i), -cache.get(i).stackSize);

                    if (change != null) {
                        changes.add(change);
                    }
                }
            }

            this.cache = newStacks;

            for (ItemStack change : changes) {
                if (change != null) {
                    if (change.stackSize > 0) {
                        network.getItemStorageCache().add(change, false);
                    } else {
                        network.getItemStorageCache().remove(change);
                    }
                }
            }
        }
    }

    public void updateForced() {
        this.cache = getStacks();
    }
}
