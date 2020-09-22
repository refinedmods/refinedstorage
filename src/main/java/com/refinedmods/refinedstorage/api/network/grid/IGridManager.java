package com.refinedmods.refinedstorage.api.network.grid;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

/**
 * Manages {@link IGridFactory} instances and has code that opens grids.
 */
public interface IGridManager {
    /**
     * @param id      the id of this factory
     * @param factory the factory
     */
    void add(ResourceLocation id, IGridFactory factory);

    /**
     * Opens a grid. Can only be called on the server.
     *
     * @param id     the grid factory id
     * @param player the player
     * @param pos    the block position
     */
    void openGrid(ResourceLocation id, ServerPlayerEntity player, BlockPos pos);

    /**
     * Opens a grid. Can only be called on the server.
     *
     * @param id     the grid factory id
     * @param player the player
     * @param stack  the stack
     * @param slotId the slot id, if applicable, otherwise -1
     */
    void openGrid(ResourceLocation id, ServerPlayerEntity player, ItemStack stack, int slotId);

    /**
     * Creates a grid.
     *
     * @param id     the grid factory id
     * @param player the player
     * @param stack  the stack, if there is one
     * @param pos    the block position, if there is one
     * @param slotId the slot id, if applicable, otherwise -1
     * @return a grid, or null if an error has occurred
     */
    @Nullable
    Pair<IGrid, TileEntity> createGrid(ResourceLocation id, PlayerEntity player, @Nullable ItemStack stack, @Nullable BlockPos pos, int slotId);
}
