package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.*;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class StorageCacheFluid implements IStorageCache<FluidStack> {
    public static final Consumer<INetwork> INVALIDATE = n -> n.getFluidStorageCache().invalidate();

    private INetwork network;
    private CopyOnWriteArrayList<IStorage<FluidStack>> storages = new CopyOnWriteArrayList<>();
    private IStackList<FluidStack> list = API.instance().createFluidStackList();
    private List<IStorageCacheListener<FluidStack>> listeners = new LinkedList<>();

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
                add(stack, stack.amount, true, false);
            }
        }

        listeners.forEach(IStorageCacheListener::onInvalidated);
    }

    @Override
    public synchronized void add(@Nonnull FluidStack stack, int size, boolean rebuilding, boolean batched) {
        list.add(stack, size);

        if (!rebuilding) {
            listeners.forEach(l -> l.onChanged(stack, size));
        }
    }

    @Override
    public synchronized void remove(@Nonnull FluidStack stack, int size, boolean batched) {
        if (list.remove(stack, size)) {
            listeners.forEach(l -> l.onChanged(stack, -size));
        }
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Cannot flush fluid storage cache");
    }

    @Override
    public void addListener(IStorageCacheListener<FluidStack> listener) {
        listeners.add(listener);

        listener.onAttached();
    }

    @Override
    public void removeListener(IStorageCacheListener<FluidStack> listener) {
        listeners.remove(listener);
    }

    @Override
    public void sort() {
        storages.sort(IStorage.COMPARATOR);
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
