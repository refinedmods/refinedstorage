package refinedstorage.tile.externalstorage;

import refinedstorage.api.storage.IStorage;

public abstract class ExternalStorage implements IStorage {
    public abstract int getCapacity();
}
