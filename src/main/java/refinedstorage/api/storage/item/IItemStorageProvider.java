package refinedstorage.api.storage.item;

import java.util.List;

/**
 * Represents a node that provides item storage to the network.
 */
public interface IItemStorageProvider {
    /**
     * Adds the item storages that this storage provider provides.
     *
     * @param storages the previously added item storages
     */
    void addItemStorages(List<IItemStorage> storages);
}
