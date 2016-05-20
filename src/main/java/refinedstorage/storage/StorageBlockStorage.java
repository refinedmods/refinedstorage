package refinedstorage.storage;

import net.minecraft.item.ItemStack;
import refinedstorage.tile.TileStorage;
import refinedstorage.tile.config.ModeConfigUtils;

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
        return ModeConfigUtils.doesNotViolateMode(storage.getInventory(), storage, storage.getCompare(), stack) && super.mayPush(stack);
    }
}
