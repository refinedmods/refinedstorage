package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class StorageCacheFluid implements IStorageCache<FluidStack> {
    private INetwork network;
    private CopyOnWriteArrayList<IStorage<FluidStack>> storages = new CopyOnWriteArrayList<>();
    private IStackList<FluidStack> list = API.instance().createFluidStackList();
    private List<BiConsumer<FluidStack, Integer>> listeners = new LinkedList<>();

    public StorageCacheFluid(INetwork network) {
        this.network = network;
    }

    @Override
    public synchronized void invalidate() {
        storages.clear();

        network.getNodeGraph().all().stream()
            .filter(node -> node.canUpdate() && node instanceof IStorageProvider)
            .forEach(node -> ((IStorageProvider) node).addFluidStorages(storages));

        list.clear();

        sort();

        for (IStorage<FluidStack> storage : storages) {
            if (storage.getAccessType() == AccessType.INSERT) {
                continue;
            }

            for (FluidStack stack : storage.getStacks()) {
                add(stack, stack.amount, true);
            }
        }

        network.sendFluidStorageToClient();
    }

    @Override
    public synchronized void add(@Nonnull FluidStack stack, int size, boolean rebuilding) {
        list.add(stack, size);

        if (!rebuilding) {
            network.sendFluidStorageDeltaToClient(stack, size);

            listeners.forEach(l -> l.accept(stack, size));
        }
    }

    @Override
    public synchronized void remove(@Nonnull FluidStack stack, int size) {
        if (list.remove(stack, size)) {
            network.sendFluidStorageDeltaToClient(stack, -size);

            listeners.forEach(l -> l.accept(stack, -size));
        }
    }

    @Override
    public void addListener(BiConsumer<FluidStack, Integer> listener) {
        listeners.add(listener);
    }

    @Override
    public void sort() {
        storages.sort(RSUtils.STORAGE_COMPARATOR);
    }

    @Override
    public IStackList<FluidStack> getList() {
        return list;
    }

    @Override
    public List<IStorage<FluidStack>> getStorages() {
        return storages;
    }
}
