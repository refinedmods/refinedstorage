package com.raoulvdberge.refinedstorage.inventory.listener;

public interface InventoryListener<T> {
    void onChanged(T handler, int slot, boolean reading);
}
