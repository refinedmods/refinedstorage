package refinedstorage.apiimpl.storage.fluid;

import net.minecraftforge.fluids.FluidStack;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.storage.fluid.IFluidStorage;
import refinedstorage.api.storage.fluid.IFluidStorageProvider;
import refinedstorage.api.storage.fluid.IGroupedFluidStorage;
import refinedstorage.api.util.IFluidStackList;
import refinedstorage.apiimpl.API;
import refinedstorage.tile.config.IAccessType;
import refinedstorage.tile.externalstorage.FluidStorageExternal;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GroupedFluidStorage implements IGroupedFluidStorage {
    private INetworkMaster network;
    private List<IFluidStorage> storages = new ArrayList<>();
    private IFluidStackList list = API.instance().createFluidStackList();

    public GroupedFluidStorage(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public synchronized void rebuild() {
        storages.clear();

        network.getNodeGraph().all().stream()
            .filter(node -> node.canUpdate() && node instanceof IFluidStorageProvider)
            .forEach(node -> ((IFluidStorageProvider) node).addFluidStorages(storages));

        list.clear();

        for (IFluidStorage storage : storages) {
            if (storage instanceof FluidStorageExternal && ((FluidStorageExternal) storage).getAccessType() == IAccessType.WRITE) {
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
