package com.refinedmods.refinedstorage.apiimpl.storage.cache;

import com.refinedmods.refinedstorage.api.storage.IStorage;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.api.util.StackListResult;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.blockentity.grid.portable.IPortableGrid;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PortableItemStorageCache implements IStorageCache<ItemStack> {
    private final IPortableGrid portableGrid;
    private final IStackList<ItemStack> list = API.instance().createItemStackList();
    private final List<IStorageCacheListener<ItemStack>> listeners = new LinkedList<>();

    public PortableItemStorageCache(IPortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    @Override
    public void invalidate(InvalidateCause cause) {
        list.clear();

        if (portableGrid.getItemStorage() != null) {
            portableGrid.getItemStorage().getStacks().forEach(list::add);
        }

        listeners.forEach(IStorageCacheListener::onInvalidated);
    }

    @Override
    public void add(@Nonnull ItemStack stack, int size, boolean rebuilding, boolean batched) {
        StackListResult<ItemStack> result = list.add(stack, size);

        if (!rebuilding) {
            listeners.forEach(l -> l.onChanged(result));
        }
    }

    @Override
    public void remove(@Nonnull ItemStack stack, int size, boolean batched) {
        StackListResult<ItemStack> result = list.remove(stack, size);

        if (result != null) {
            listeners.forEach(l -> l.onChanged(result));
        }
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException();
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
    public void reAttachListeners() {
        listeners.forEach(IStorageCacheListener::onAttached);
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
    public IStackList<ItemStack> getCraftablesList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IStorage<ItemStack>> getStorages() {
        return Collections.emptyList();
    }
}
