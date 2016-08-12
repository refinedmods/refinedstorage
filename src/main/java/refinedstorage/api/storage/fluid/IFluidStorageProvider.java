package refinedstorage.api.storage.fluid;

import java.util.List;

/**
 * Represents a tile that provides item storage to the network. Implement this on a tile that is a {@link refinedstorage.api.network.INetworkNode}.
 */
public interface IFluidStorageProvider {
    /**
     * Adds the fluid storages that this storage provider provides.
     *
     * @param storages The previously added fluid storages
     */
    void addFluidStorages(List<IFluidStorage> storages);
}
