package com.refinedmods.refinedstorage.api.storage.externalstorage

import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.Direction


/**
 * Provides an external storage handler to the external storage block.
 *
 * @param <T>
</T> */
interface IExternalStorageProvider<T> {
    /**
     * @param tile      the tile
     * @param direction the direction of the external storage
     * @return true if the provider can provide, false otherwise
     */
    fun canProvide(tile: BlockEntity, direction: Direction): Boolean

    /**
     * @param context   the context of the external storage
     * @param tile      the tile
     * @param direction the direction of the external storage
     * @return the external storage handler
     */
    fun provide(context: IExternalStorageContext, tile: BlockEntity, direction: Direction): IExternalStorage<T>

    /**
     * Returns the priority of this external storage provider.
     * The one with the highest priority is chosen.
     * Refined Storage's default handlers for [net.minecraftforge.items.IItemHandler] and [net.minecraftforge.fluids.capability.IFluidHandler] return 0.
     * This value can't be dynamic (only fixed), since the sorted order is cached.
     *
     * @return the priority
     */
    fun getPriority(): Int
}