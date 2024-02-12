package com.refinedmods.refinedstorage.blockentity.grid.portable;

import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import javax.annotation.Nullable;

public interface IPortableGrid {
    @Nullable
    IStorageCache getCache();

    default IStorageCache<ItemStack> getItemCache() {
        return getCache();
    }

    default IStorageCache<FluidStack> getFluidCache() {
        return getCache();
    }

    @Nullable
    IStorageDisk getStorage();

    default IStorageDisk<ItemStack> getItemStorage() {
        return getStorage();
    }

    default IStorageDisk<FluidStack> getFluidStorage() {
        return getStorage();
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
