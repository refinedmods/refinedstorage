package refinedstorage.api.storage;

import java.util.List;

/**
 * Should be implemented as a capability on tile entities.
 *
 * @see refinedstorage.api.RefinedStorageCapabilities#STORAGE_PROVIDER_CAPABILITY
 */
public interface IStorageProvider {
    /**
     * @param storages A list containing previously added storages
     */
    void provide(List<IStorage> storages);
}
