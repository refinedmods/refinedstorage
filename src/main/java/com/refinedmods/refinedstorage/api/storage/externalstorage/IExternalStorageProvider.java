package com.refinedmods.refinedstorage.api.storage.externalstorage;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Provides an external storage handler to the external storage block.
 *
 * @param <T>
 */
public interface IExternalStorageProvider<T> {
    /**
     * @param level the level
     * @param pos   the position
     * @return true if the provider can provide, false otherwise
     */
    boolean canProvide(Level level, BlockPos pos, Direction direction);

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
     * Refined Storage's default handlers for {@link net.neoforged.neoforge.items.IItemHandler} and {@link net.neoforged.neoforge.fluids.capability.IFluidHandler} return 0.
     * This value can't be dynamic (only fixed), since the sorted order is cached.
     *
     * @return the priority
     */
    int getPriority();
}
