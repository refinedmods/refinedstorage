package com.refinedmods.refinedstorage.tile.grid.portable;

import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public interface IPortableGrid {
    @Nullable
    IStorageCache getCache();

    default IStorageCache<ItemStack> getItemCache() {
        return (IStorageCache<ItemStack>) getCache();
    }

    default IStorageCache<FluidStack> getFluidCache() {
        return (IStorageCache<FluidStack>) getCache();
    }

    @Nullable
    IStorageDisk getStorage();

    default IStorageDisk<ItemStack> getItemStorage() {
        return (IStorageDisk<ItemStack>) getStorage();
    }

    default IStorageDisk<FluidStack> getFluidStorage() {
        return (IStorageDisk<FluidStack>) getStorage();
    }

    void drainEnergy(int energy);

    int getEnergy();

    BaseItemHandler getDiskInventory();

    IItemHandlerModifiable getFilter();

    IStorageTracker<ItemStack> getItemStorageTracker();

    IStorageTracker<FluidStack> getFluidStorageTracker();

    boolean isGridActive();

    PortableGridDiskState getDiskState();
}
