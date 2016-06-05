package refinedstorage.api.storage;

import java.util.List;

/**
 * Should be implement as a capability on tile entities.
 */
public interface IStorageProvider {
    /**
     * @param storages A list containing previously added storages
     */
    void provide(List<IStorage> storages);
}
