package com.refinedmods.refinedstorage.api.network.grid;

import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

/**
 * Creates a grid.
 */
public interface IGridFactory {
    /**
     * Creates a grid from a stack. Used when {@link #getType()} is STACK.
     *
     * @param player the player
     * @param stack  the stack
     * @param slot   the slot in the players inventory or curio slot, otherwise -1
     * @return the grid, or null if a problem occurred
     */
    @Nullable
    IGrid createFromStack(Player player, ItemStack stack, PlayerSlot slot);

    /**
     * Creates a grid from a block. Used when {@link #getType()} is BLOCK.
     *
     * @param player the player
     * @param pos    the block position
     * @return the grid, or null if a problem occurred
     */
    @Nullable
    IGrid createFromBlock(Player player, BlockPos pos);

    /**
     * Returns a possible tile for this grid if {@link #getType()} is BLOCK.
     *
     * @param world the world
     * @param pos   the position
     * @return the tile, or null if no tile is required
     */
    @Nullable
    BlockEntity getRelevantTile(Level world, BlockPos pos);

    /**
     * @return the type
     */
    GridFactoryType getType();
}
