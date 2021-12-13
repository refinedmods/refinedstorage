package com.refinedmods.refinedstorage.api.storage.externalstorage;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

/**
 * Provides an external storage handler to the external storage block.
 *
 * @param <T>
 */
public interface IExternalStorageProvider<T> {
    /**
     * @param blockEntity the block entity
     * @param direction   the direction of the external storage
     * @return true if the provider can provide, false otherwise
     */
    boolean canProvide(BlockEntity blockEntity, Direction direction);

    /**
     * @param context     the context of the external storage
     * @param blockEntity the block entity
     * @param direction   the direction of the external storage
     * @return the external storage handler
     */
    @Nonnull
    IExternalStorage<T> provide(IExternalStorageContext context, BlockEntity blockEntity, Direction direction);

    /**
     * Returns the priority of this external storage provider.
     * The one with the highest priority is chosen.
     * Refined Storage's default handlers for {@link net.minecraftforge.items.IItemHandler} and {@link net.minecraftforge.fluids.capability.IFluidHandler} return 0.
     * This value can't be dynamic (only fixed), since the sorted order is cached.
     *
     * @return the priority
     */
    int getPriority();
}
