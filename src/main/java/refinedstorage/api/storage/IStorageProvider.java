package refinedstorage.api.storage;

import java.util.List;

/**
 * Implement this interface on the tile that has a {@link refinedstorage.api.RefinedStorageCapabilities#NETWORK_SLAVE_CAPABILITY} capability.
 */
public interface IStorageProvider {
    /**
     * @param storages A list containing previously added storages
     */
    void provide(List<IStorage> storages);
}
