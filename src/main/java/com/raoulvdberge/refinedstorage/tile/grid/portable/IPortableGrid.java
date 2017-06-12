package com.raoulvdberge.refinedstorage.tile.grid.portable;

import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageDisk;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public interface IPortableGrid {
    IStorageCache<ItemStack> getCache();

    @Nullable
    IStorageDisk<ItemStack> getStorage();

    List<EntityPlayer> getWatchers();

    void drainEnergy(int energy);

    int getEnergy();

    ItemHandlerBase getDisk();

    ItemHandlerBase getFilter();
}
