package refinedstorage.api.storage;

import java.util.List;

/**
 * Represents a tile that provides storage to the network. Implement this on a tile that implements {@link refinedstorage.api.network.INetworkNode}.
 */
public interface IStorageProvider {
    /**
     * Adds the storages that this storage provider provides.
     *
     * @param storages The previously added storages
     */
    void addStorages(List<IStorage> storages);
}
