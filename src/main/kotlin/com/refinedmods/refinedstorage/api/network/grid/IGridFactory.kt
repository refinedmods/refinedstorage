package com.refinedmods.refinedstorage.api.network.grid

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


/**
 * Creates a grid.
 */
interface IGridFactory {
    /**
     * Creates a grid from a stack. Used when [.getType] is STACK.
     *
     * @param player the player
     * @param stack  the stack
     * @param slotId the slot id, if applicable, otherwise -1
     * @return the grid, or null if a problem occurred
     */
    fun createFromStack(player: PlayerEntity, stack: ItemStack, slotId: Int): IGrid?

    /**
     * Creates a grid from a block. Used when [.getType] is BLOCK.
     *
     * @param player the player
     * @param pos    the block position
     * @return the grid, or null if a problem occurred
     */
    fun createFromBlock(player: PlayerEntity, pos: BlockPos): IGrid?

    /**
     * Returns a possible tile for this grid if [.getType] is BLOCK.
     *
     * @param world the world
     * @param pos   the position
     * @return the tile, or null if no tile is required
     */
    fun getRelevantTile(world: World, pos: BlockPos): BlockEntity?

    /**
     * @return the type
     */
    val type: GridFactoryType
}