package com.refinedmods.refinedstorage.api.network.grid;

import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
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
    void openGrid(ResourceLocation id, ServerPlayer player, BlockPos pos);

    /**
     * Opens a grid. Can only be called on the server.
     *
     * @param id     the grid factory id
     * @param player the player
     * @param stack  the stack
     * @param slot   the slot in the players inventory or curio slot, otherwise -1
     */
    void openGrid(ResourceLocation id, ServerPlayer player, ItemStack stack, PlayerSlot slot);

    /**
     * Creates a grid.
     *
     * @param id     the grid factory id
     * @param player the player
     * @param stack  the stack, if there is one
     * @param pos    the block position, if there is one
     * @param slot   the slot in the players inventory,or curio slot, otherwise -1
     * @return a grid, or null if an error has occurred
     */
    @Nullable
    Pair<IGrid, BlockEntity> createGrid(ResourceLocation id, Player player, @Nullable ItemStack stack, @Nullable BlockPos pos, PlayerSlot slot);
}
