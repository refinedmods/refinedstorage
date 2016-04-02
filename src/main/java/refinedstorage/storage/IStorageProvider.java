package refinedstorage.storage;

import java.util.List;

public interface IStorageProvider {
    void provide(List<IStorage> storages);
}
