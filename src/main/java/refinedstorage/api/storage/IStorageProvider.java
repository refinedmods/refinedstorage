package refinedstorage.api.storage;

import java.util.List;

public interface IStorageProvider {
    /**
     * @param storages A list containing previously added storages
     */
    void provide(List<IStorage> storages);
}
