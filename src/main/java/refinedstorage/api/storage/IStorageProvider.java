package refinedstorage.api.storage;

import java.util.List;

/**
 * Implement this interface on the tile that has a {@link refinedstorage.api.RefinedStorageCapabilities#NETWORK_SLAVE_CAPABILITY} capability.
 */
public interface IStorageProvider {
    /**
     * Adds the storages.
     *
     * @param storages The previously added storages
     */
    void addStorages(List<IStorage> storages);
}
