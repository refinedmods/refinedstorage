package com.raoulvdberge.refinedstorage.tile.grid.portable;

import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDisk;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.tile.data.TileDataWatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.List;

public interface IPortableGrid {
    IStorageCache<ItemStack> getCache();

    @Nullable
    IStorageDisk<ItemStack> getStorage();

    List<TileDataWatcher> getWatchers();

    void drainEnergy(int energy);

    int getEnergy();

    ItemHandlerBase getDisk();

    IItemHandlerModifiable getFilter();
}
