package refinedstorage.api.storage;

/**
 * Implement this interface on the tile that has a {@link refinedstorage.api.RefinedStorageCapabilities#NETWORK_SLAVE_CAPABILITY} capability.
 */
public interface IStorageProvider {
    /**
     * @return The storages that this tile provides.
     */
    IStorage[] getStorages();
}
