package refinedstorage.tile.externalstorage;

import net.minecraft.item.ItemStack;
import refinedstorage.api.storage.item.IItemStorage;
import refinedstorage.apiimpl.API;

import java.util.List;

public abstract class ItemStorageExternal implements IItemStorage {
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
                if (!API.instance().getComparer().isEqual(items.get(i), cache.get(i))) {
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
