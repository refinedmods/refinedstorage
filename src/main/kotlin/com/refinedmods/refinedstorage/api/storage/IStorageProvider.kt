package com.refinedmods.refinedstorage.api.storage

import net.minecraft.item.ItemStack
import reborncore.common.fluid.container.FluidInstance


/**
 * Represents a node that provides the network with storage.
 */
interface IStorageProvider {
    /**
     * @param storages the item storages
     */
    fun addItemStorages(storages: List<IStorage<ItemStack>>)

    /**
     * @param storages the fluid storages
     */
    fun addFluidStorages(storages: List<IStorage<FluidInstance>>)
}