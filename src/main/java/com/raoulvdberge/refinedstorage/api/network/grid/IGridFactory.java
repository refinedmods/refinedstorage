package com.raoulvdberge.refinedstorage.api.network.grid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
     * @return the grid, or null if a problem occurred
     */
    @Nullable
    IGrid createFromStack(EntityPlayer player, ItemStack stack);

    /**
     * Creates a grid from a block. Used when {@link #getType()} is BLOCK.
     *
     * @param player the player
     * @param pos    the block position
     * @return the grid, or null if a problem occurred
     */
    @Nullable
    IGrid createFromBlock(EntityPlayer player, BlockPos pos);

    /**
     * Returns a possible tile for this grid if {@link #getType()} is BLOCK.
     *
     * @param world the world
     * @param pos   the position
     * @return the tile, or null if no tile is required
     */
    @Nullable
    TileEntity getRelevantTile(World world, BlockPos pos);

    /**
     * @return the type
     */
    GridFactoryType getType();
}
