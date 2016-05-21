package refinedstorage.storage;

import net.minecraft.item.ItemStack;
import refinedstorage.tile.TileStorage;
import refinedstorage.tile.config.ModeFilter;

public class StorageBlockStorage extends NBTStorage {
    private TileStorage storage;

    public StorageBlockStorage(TileStorage storage) {
        super(storage.getStorageTag(), storage.getCapacity());

        this.storage = storage;
    }

    @Override
    public int getPriority() {
        return storage.getPriority();
    }

    @Override
    public boolean mayPush(ItemStack stack) {
        return ModeFilter.respectsMode(storage.getInventory(), storage, storage.getCompare(), stack) && super.mayPush(stack);
    }
}
