package com.refinedmods.refinedstorage.api.network.grid.handler

import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*


/**
 * Defines the behavior of fluid grids.
 */
interface IFluidGridHandler {
    /**
     * Called when a player tries to extract a fluid from the grid.
     *
     * @param player the player that is attempting the extraction
     * @param id     the id of the fluid we're trying to extract, this id is the id from [StackListEntry]
     * @param shift  true if shift click was used, false otherwise
     */
    fun onExtract(player: ServerPlayerEntity, id: UUID, shift: Boolean)

    /**
     * Called when a player tries to insert fluids in the grid.
     *
     * @param player    the player
     * @param container a stack with a fluid container we're trying to insert
     * @return the remainder, or an empty stack if there is no remainder
     */
    fun onInsert(player: ServerPlayerEntity, container: ItemStack): ItemStack

    /**
     * Called when a player is trying to insert a fluid that it is holding in their hand in the GUI.
     *
     * @param player the player that is attempting the insert
     */
    fun onInsertHeldContainer(player: ServerPlayerEntity)

    /**
     * Called when a player requests the crafting preview window to be opened.
     *
     * @param player    the player
     * @param id        the id of the fluid we're trying to extract, this id is the id from [StackListEntry]
     * @param quantity  the amount of that item that we need a preview for
     * @param noPreview true if the crafting preview window shouldn't be shown, false otherwise
     */
    fun onCraftingPreviewRequested(player: ServerPlayerEntity, id: UUID, quantity: Int, noPreview: Boolean)

    /**
     * Called when a player requested crafting for an item.
     *
     * @param player   the player that is requesting the crafting
     * @param id       the id of the fluid we're trying to extract, this id is the id from [StackListEntry]
     * @param quantity the amount of the item that has to be crafted
     */
    fun onCraftingRequested(player: ServerPlayerEntity, id: UUID, quantity: Int)
}