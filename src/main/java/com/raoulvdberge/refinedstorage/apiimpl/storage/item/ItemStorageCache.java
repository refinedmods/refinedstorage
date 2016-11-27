package com.raoulvdberge.refinedstorage.apiimpl.storage.item;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.item.IItemStorage;
import com.raoulvdberge.refinedstorage.api.storage.item.IItemStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.item.IItemStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IItemStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemStorageCache implements IItemStorageCache {
    private INetworkMaster network;
    private List<IItemStorage> storages = new ArrayList<>();
    private IItemStackList list = API.instance().createItemStackList();

    public ItemStorageCache(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public synchronized void invalidate() {
        storages.clear();

        network.getNodeGraph().all().stream()
            .filter(node -> node.canUpdate() && node instanceof IItemStorageProvider)
            .forEach(node -> ((IItemStorageProvider) node).addItemStorages(storages));

        list.clear();

        for (IItemStorage storage : storages) {
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
    public IItemStackList getList() {
        return list;
    }

    @Override
    public List<IItemStorage> getStorages() {
        return storages;
    }
}
