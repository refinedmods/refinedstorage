package refinedstorage.tile.externalstorage;

import net.minecraft.item.ItemStack;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.api.storage.IStorage;

import java.util.List;

public abstract class ExternalStorage implements IStorage {
    private List<ItemStack> cache;

    public abstract int getCapacity();

    public boolean updateCache() {
        List<ItemStack> items = getItems();

        if (cache == null) {
            cache = items;
        } else if (items.size() != cache.size()) {
            cache = items;

            return true;
        } else {
            for (int i = 0; i < items.size(); ++i) {
                if (!CompareUtils.compareStack(items.get(i), cache.get(i))) {
                    cache = items;

                    return true;
                }
            }
        }

        return false;
    }

    public void updateCacheForcefully() {
        cache = getItems();
    }
}
