package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.*;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class StorageCacheItem implements IStorageCache<ItemStack> {
    public static final Consumer<INetwork> INVALIDATE = network -> network.getItemStorageCache().invalidate();

    private INetwork network;
    private CopyOnWriteArrayList<IStorage<ItemStack>> storages = new CopyOnWriteArrayList<>();
    private IStackList<ItemStack> list = API.instance().createItemStackList();
    private List<IStorageCacheListener<ItemStack>> listeners = new LinkedList<>();
    private List<Pair<ItemStack, Integer>> batchedChanges = new ArrayList<>();

    public StorageCacheItem(INetwork network) {
        this.network = network;
    }

    @Override
    public synchronized void invalidate() {
        storages.clear();

        network.getNodeGraph().all().stream()
            .filter(node -> node.canUpdate() && node instanceof IStorageProvider)
            .forEach(node -> ((IStorageProvider) node).addItemStorages(storages));

        list.clear();

        sort();

        for (IStorage<ItemStack> storage : storages) {
            if (storage.getAccessType() == AccessType.INSERT) {
                continue;
            }

            for (ItemStack stack : storage.getStacks()) {
                if (!stack.isEmpty()) {
                    add(stack, stack.getCount(), true, false);
                }
            }
        }

        listeners.forEach(IStorageCacheListener::onInvalidated);
    }

    @Override
    public synchronized void add(@Nonnull ItemStack stack, int size, boolean rebuilding, boolean batched) {
        if(size !=0) {
            list.add(stack, size);
        }

        if (!rebuilding) {
            if (!batched) {
                listeners.forEach(l -> l.onChanged(stack, size));
            } else {
                batchedChanges.add(Pair.of(stack.copy(), size));
            }
        }
    }

    @Override
    public synchronized void remove(@Nonnull ItemStack stack, int size, boolean batched) {
        if (list.remove(stack, size)) {
            if (!batched) {
                listeners.forEach(l -> l.onChanged(stack, -size));
            } else {
                batchedChanges.add(Pair.of(stack.copy(), -size));
            }
        }
    }

    @Override
    public synchronized void flush() {
        if (!batchedChanges.isEmpty()) {
            if (batchedChanges.size() > 1) {
                listeners.forEach(l -> l.onChangedBulk(batchedChanges));
            } else {
                batchedChanges.forEach(c -> listeners.forEach(l -> l.onChanged(c.getKey(), c.getValue())));
            }

            batchedChanges.clear();
        }
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
        storages.sort(IStorage.COMPARATOR);
    }

    @Override
    public IStackList<ItemStack> getList() {
        return list;
    }

    @Override
    public List<IStorage<ItemStack>> getStorages() {
        return storages;
    }
}
