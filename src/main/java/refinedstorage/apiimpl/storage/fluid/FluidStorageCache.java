package refinedstorage.apiimpl.storage.fluid;

import net.minecraftforge.fluids.FluidStack;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.AccessType;
import refinedstorage.api.storage.fluid.IFluidStorage;
import refinedstorage.api.storage.fluid.IFluidStorageCache;
import refinedstorage.api.storage.fluid.IFluidStorageProvider;
import refinedstorage.api.util.IFluidStackList;
import refinedstorage.apiimpl.API;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FluidStorageCache implements IFluidStorageCache {
    private INetworkMaster network;
    private List<IFluidStorage> storages = new ArrayList<>();
    private IFluidStackList list = API.instance().createFluidStackList();

    public FluidStorageCache(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public synchronized void invalidate() {
        storages.clear();

        network.getNodeGraph().all().stream()
            .filter(node -> node.canUpdate() && node instanceof IFluidStorageProvider)
            .forEach(node -> ((IFluidStorageProvider) node).addFluidStorages(storages));

        list.clear();

        for (IFluidStorage storage : storages) {
            if (storage.getAccessType() == AccessType.WRITE) {
                continue;
            }

            for (FluidStack stack : storage.getStacks()) {
                add(stack, true);
            }
        }

        network.sendFluidStorageToClient();
    }

    @Override
    public synchronized void add(@Nonnull FluidStack stack, boolean rebuilding) {
        list.add(stack);

        if (!rebuilding) {
            network.sendFluidStorageDeltaToClient(stack, stack.amount);
        }
    }

    @Override
    public synchronized void remove(@Nonnull FluidStack stack) {
        if (list.remove(stack, true)) {
            network.sendFluidStorageDeltaToClient(stack, -stack.amount);
        }
    }

    @Override
    public IFluidStackList getList() {
        return list;
    }

    @Override
    public List<IFluidStorage> getStorages() {
        return storages;
    }
}
