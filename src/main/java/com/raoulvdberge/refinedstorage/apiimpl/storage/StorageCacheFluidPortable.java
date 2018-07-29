package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCacheListener;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.tile.grid.portable.IPortableGrid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StorageCacheFluidPortable implements IStorageCache<FluidStack> {
    private IPortableGrid portableGrid;
    private IStackList<FluidStack> list = API.instance().createFluidStackList();
    private List<IStorageCacheListener<FluidStack>> listeners = new LinkedList<>();

    public StorageCacheFluidPortable(IPortableGrid portableGrid) {
        this.portableGrid = portableGrid;
    }

    @Override
    public void invalidate() {
        list.clear();

        if (portableGrid.getFluidStorage() != null) {
            portableGrid.getFluidStorage().getStacks().forEach(list::add);
        }

        listeners.forEach(IStorageCacheListener::onInvalidated);
    }

    @Override
    public void add(@Nonnull FluidStack stack, int size, boolean rebuilding, boolean batched) {
        list.add(stack, size);

        if (!rebuilding) {
            listeners.forEach(l -> l.onChanged(stack, size));
        }
    }

    @Override
    public void remove(@Nonnull FluidStack stack, int size, boolean batched) {
        if (list.remove(stack, size)) {
            listeners.forEach(l -> l.onChanged(stack, -size));
        }
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Cannot flush portable grid storage cache");
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
        // NO OP
    }

    @Override
    public IStackList<FluidStack> getList() {
        return list;
    }

    @Override
    public List<IStorage<FluidStack>> getStorages() {
        return Collections.emptyList();
    }
}
