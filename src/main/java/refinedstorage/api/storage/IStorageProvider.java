package refinedstorage.api.storage;

import refinedstorage.api.storage.item.IItemStorage;

import java.util.List;

/**
 * Represents a tile that provides storage to the network. Implement this on a tile that is a {@link refinedstorage.api.network.INetworkNode}.
 */
public interface IStorageProvider {
    /**
     * Adds the item storages that this storage provider provides.
     *
     * @param storages The previously added item storages
     */
    void addItemStorages(List<IItemStorage> storages);
}
