package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorageCacheItem implements IStorageCache<ItemStack> {
    private INetworkMaster network;
    private List<IStorage<ItemStack>> storages = Collections.synchronizedList(new ArrayList<>());
    private IStackList<ItemStack> list = API.instance().createItemStackList();

    public StorageCacheItem(INetworkMaster network) {
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
    }

    @Override
    public synchronized void remove(@Nonnull ItemStack stack, int size) {
        if (list.remove(stack, size)) {
            network.sendItemStorageDeltaToClient(stack, -size);
        }
    }

    @Override
    public void sort() {
        storages.sort(RSUtils.STORAGE_COMPARATOR);
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
