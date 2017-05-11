package com.raoulvdberge.refinedstorage.tile.grid.portable;

import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IPortableGrid {
    IStorageCache<ItemStack> getCache();

    IStorage<ItemStack> getStorage();

    List<EntityPlayer> getWatchers();

    void drainEnergy(int energy);

    ItemHandlerBase getDisk();

    ItemHandlerBase getFilter();
}
