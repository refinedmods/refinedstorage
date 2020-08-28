package com.refinedmods.refinedstorage.api.network.grid

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import org.apache.commons.lang3.tuple.Pair


/**
 * Manages [IGridFactory] instances and has code that opens grids.
 */
interface IGridManager {
    /**
     * @param id      the id of this factory
     * @param factory the factory
     */
    fun add(id: Identifier, factory: IGridFactory)

    /**
     * Opens a grid. Can only be called on the server.
     *
     * @param id     the grid factory id
     * @param player the player
     * @param pos    the block position
     */
    fun openGrid(id: Identifier, player: ServerPlayerEntity, pos: BlockPos)

    /**
     * Opens a grid. Can only be called on the server.
     *
     * @param id     the grid factory id
     * @param player the player
     * @param stack  the stack
     * @param slotId the slot id, if applicable, otherwise -1
     */
    fun openGrid(id: Identifier, player: ServerPlayerEntity, stack: ItemStack, slotId: Int)

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
    fun createGrid(id: Identifier, player: PlayerEntity, stack: ItemStack?, pos: BlockPos?, slotId: Int): Pair<IGrid, BlockEntity>?
}