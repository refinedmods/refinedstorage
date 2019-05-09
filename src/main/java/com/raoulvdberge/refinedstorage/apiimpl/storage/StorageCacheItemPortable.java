package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StorageCacheItemPortable implements IStorageCache<ItemStack> {
    private IPortableGrid portableGrid;
    private IStackList<ItemStack> list = API.instance().createItemStackList();
    private List<IStorageCacheListener<ItemStack>> listeners = new LinkedList<>();

    public StorageCacheItemPortable(IPortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    @Override
    public void invalidate() {
        list.clear();

        if (portableGrid.getItemStorage() != null) {
            portableGrid.getItemStorage().getStacks().forEach(list::add);
        }

        listeners.forEach(IStorageCacheListener::onInvalidated);
    }

    @Override
    public void add(@Nonnull ItemStack stack, int size, boolean rebuilding, boolean batched) {
        if(size !=0) {
            list.add(stack, size);
        }

        if (!rebuilding) {
            listeners.forEach(l -> l.onChanged(stack, size));
        }
    }

    @Override
    public void remove(@Nonnull ItemStack stack, int size, boolean batched) {
        if (list.remove(stack, size)) {
            listeners.forEach(l -> l.onChanged(stack, -size));
        }
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Cannot flush portable grid storage cache");
    }

    @Override
    public void addListener(IStorageCacheListener<ItemStack> listener) {
        listeners.add(listener);

        listener.onAttached();
    }

    @Override
    public void removeListener(IStorageCacheListener<ItemStack> listener) {
        listeners.remove(listener);
    }

    @Override
    public void sort() {
        // NO OP
    }

    @Override
    public IStackList<ItemStack> getList() {
        return list;
    }

    @Override
    public List<IStorage<ItemStack>> getStorages() {
        return Collections.emptyList();
    }
}
