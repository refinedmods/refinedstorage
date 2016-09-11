package refinedstorage.api.storage.fluid;

import java.util.List;

/**
 * Represents a node that provides fluid storage to the network.
 */
public interface IFluidStorageProvider {
    /**
     * Adds the fluid storages that this storage provider provides.
     *
     * @param storages the previously added fluid storages
     */
    void addFluidStorages(List<IFluidStorage> storages);
}
