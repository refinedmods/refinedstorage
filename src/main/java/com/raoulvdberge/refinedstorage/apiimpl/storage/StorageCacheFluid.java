package com.raoulvdberge.refinedstorage.apiimpl.storage;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class StorageCacheFluid implements IStorageCache<FluidStack> {
    private INetworkMaster network;
    private List<IStorage<FluidStack>> storages = new ArrayList<>();
    private IStackList<FluidStack> list = API.instance().createFluidStackList();

    public StorageCacheFluid(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public synchronized void invalidate() {
        storages.clear();

        network.getNodeGraph().all().stream()
            .filter(node -> node.canUpdate() && node instanceof IStorageProvider)
            .forEach(node -> ((IStorageProvider) node).addFluidStorages(storages));

        list.clear();

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
        }
    }

    @Override
    public synchronized void remove(@Nonnull FluidStack stack, int size) {
        if (list.remove(stack, size)) {
            network.sendFluidStorageDeltaToClient(stack, -size);
        }
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
