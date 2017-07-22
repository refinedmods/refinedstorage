package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class StorageCacheItem implements IStorageCache<ItemStack> {
    private INetwork network;
    private CopyOnWriteArrayList<IStorage<ItemStack>> storages = new CopyOnWriteArrayList<>();
    private IStackList<ItemStack> list = API.instance().createItemStackList();
    private List<BiConsumer<ItemStack, Integer>> listeners = new LinkedList<>();

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
                    add(stack, stack.getCount(), true);
                }
            }
        }

        network.sendItemStorageToClient();
    }

    @Override
    public synchronized void add(@Nonnull ItemStack stack, int size, boolean rebuilding) {
        list.add(stack, size);

        if (!rebuilding) {
            network.sendItemStorageDeltaToClient(stack, size);
        }

        listeners.forEach(l -> l.accept(stack, size));
    }

    @Override
    public synchronized void remove(@Nonnull ItemStack stack, int size) {
        if (list.remove(stack, size)) {
            network.sendItemStorageDeltaToClient(stack, -size);

            listeners.forEach(l -> l.accept(stack, -size));
        }
    }

    @Override
    public void addListener(BiConsumer<ItemStack, Integer> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(BiConsumer<ItemStack, Integer> listener) {
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
