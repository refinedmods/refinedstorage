package refinedstorage.tile.externalstorage;

import refinedstorage.api.storage.IStorage;

public abstract class ExternalStorage implements IStorage {
    private int hash = -1;

    public abstract int getCapacity();

    public void setHash() {
        this.hash = getHash();
    }

    public abstract int getHash();

    public boolean isDirty() {
        if (hash != -1 && hash != getHash()) {
            hash = getHash();

            return true;
        }

        return false;
    }
}
