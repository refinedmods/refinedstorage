package com.raoulvdberge.refinedstorage.tile.grid.portable;

import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public interface IPortableGrid {
    interface IPortableGridRenderInfo {
        int getStored();

        int getCapacity();

        boolean hasStorage();

        boolean isActive();
    }

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

    ItemHandlerBase getDisk();

    IItemHandlerModifiable getFilter();

    IStorageTracker<ItemStack> getItemStorageTracker();

    IStorageTracker<FluidStack> getFluidStorageTracker();
}
