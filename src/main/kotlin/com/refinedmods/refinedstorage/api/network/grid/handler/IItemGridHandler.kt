package com.refinedmods.refinedstorage.api.network.grid.handler

import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*


/**
 * Defines the behavior of item grids.
 */
interface IItemGridHandler {
    /**
     * Called when a player tries to extract an item from the grid.
     *
     * @param player the player that is attempting the extraction
     * @param id     the id of the item we're trying to extract, this id is the id from [StackListEntry]
     * @param flags  how we are extracting, see the flags in [IItemGridHandler]
     */
    fun onExtract(player: ServerPlayerEntity?, id: UUID?, flags: Int)

    /**
     * Called when a player tries to insert an item in the grid.
     *
     * @param player the player that is attempting the insert
     * @param stack  the item we're trying to insert
     * @return the remainder, or an empty stack if there is no remainder
     */
    fun onInsert(player: ServerPlayerEntity, stack: ItemStack): ItemStack

    /**
     * Called when a player is trying to insert an item that it is holding in their hand in the GUI.
     *
     * @param player the player that is attempting the insert
     * @param single true if we are only inserting a single item, false otherwise
     */
    fun onInsertHeldItem(player: ServerPlayerEntity, single: Boolean)

    /**
     * Called when a player requests the crafting preview window to be opened.
     *
     * @param player    the player
     * @param id        the id of the item we're trying to extract, this id is the id from [StackListEntry]
     * @param quantity  the amount of that item that we need a preview for
     * @param noPreview true if the crafting preview window shouldn't be shown, false otherwise
     */
    fun onCraftingPreviewRequested(player: ServerPlayerEntity, id: UUID, quantity: Int, noPreview: Boolean)

    /**
     * Called when a player requested crafting for an item.
     *
     * @param player   the player that is requesting the crafting
     * @param id       the id of the item we're trying to extract, this id is the id from [StackListEntry]
     * @param quantity the amount of the item that has to be crafted
     */
    fun onCraftingRequested(player: ServerPlayerEntity, id: UUID, quantity: Int)

    /**
     * Called when a player wants to cancel a crafting task.
     *
     * @param player the player that requested the cancel
     * @param id     the task id, or null to cancel all tasks that are in the network currently
     */
    fun onCraftingCancelRequested(player: ServerPlayerEntity,jid: UUID?)

    companion object {
        const val EXTRACT_HALF = 1
        const val EXTRACT_SINGLE = 2
        const val EXTRACT_SHIFT = 4
    }
}