package com.raoulvdberge.refinedstorage.api.storage;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Represents a node that provides the network with storage.
 */
public interface IStorageProvider {
    /**
     * @param cache the storage cache
     */
    void addItemStorages(IStorageCache<ItemStack> cache);

    /**
     * @param cache the storage cache
     */
    void addFluidStorages(IStorageCache<FluidStack> cache);
}
