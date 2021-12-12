package com.refinedmods.refinedstorage.api.network.grid.handler;

import com.refinedmods.refinedstorage.api.util.StackListEntry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Defines the behavior of item grids.
 */
public interface IItemGridHandler {
    int EXTRACT_HALF = 1;
    int EXTRACT_SINGLE = 2;
    int EXTRACT_SHIFT = 4;

    /**
     * Called when a player tries to extract an item from the grid through the Inventory
     *
     * @param player        the player that is attempting the extraction
     * @param stack         the stack we're trying to extract
     * @param preferredSlot playerInventory slot to prefer when adding or -1
     * @param flags         how we are extracting, see the flags in {@link IItemGridHandler}
     */
    void onExtract(ServerPlayer player, ItemStack stack, int preferredSlot, int flags);

    /**
     * Called when a player tries to extract an item from the grid.
     *
     * @param player        the player that is attempting the extraction
     * @param id            the id of the item we're trying to extract, this id is the id from {@link StackListEntry}
     * @param preferredSlot playerInventory slot to prefer when adding or -1
     * @param flags         how we are extracting, see the flags in {@link IItemGridHandler}
     */
    void onExtract(ServerPlayer player, UUID id, int preferredSlot, int flags);

    /**
     * Called when a player tries to insert an item in the grid.
     *
     * @param player the player that is attempting the insert
     * @param stack  the item we're trying to insert
     * @param single true if we are only inserting a single item, false otherwise
     * @return the remainder, or an empty stack if there is no remainder
     */
    @Nonnull
    ItemStack onInsert(ServerPlayer player, ItemStack stack, boolean single);

    /**
     * Called when a player is trying to insert an item that it is holding in their hand in the GUI.
     *
     * @param player the player that is attempting the insert
     * @param single true if we are only inserting a single item, false otherwise
     */
    void onInsertHeldItem(ServerPlayer player, boolean single);

    /**
     * Called when a player requests the crafting preview window to be opened.
     *
     * @param player    the player
     * @param id        the id of the item we're trying to extract, this id is the id from {@link StackListEntry}
     * @param quantity  the amount of that item that we need a preview for
     * @param noPreview true if the crafting preview window shouldn't be shown, false otherwise
     */
    void onCraftingPreviewRequested(ServerPlayer player, UUID id, int quantity, boolean noPreview);

    /**
     * Called when a player requested crafting for an item.
     *
     * @param player   the player that is requesting the crafting
     * @param id       the id of the item we're trying to extract, this id is the id from {@link StackListEntry}
     * @param quantity the amount of the item that has to be crafted
     */
    void onCraftingRequested(ServerPlayer player, UUID id, int quantity);

    /**
     * Called when a player wants to cancel a crafting task.
     *
     * @param player the player that requested the cancel
     * @param id     the task id, or null to cancel all tasks that are in the network currently
     */
    void onCraftingCancelRequested(ServerPlayer player, @Nullable UUID id);

    /**
     * Called when a player shift or ctrl scrolls in the player inventory
     *
     * @param player player that is scrolling
     * @param slot   slot the mouse is hovering over
     * @param shift  if true shift is pressed, if false ctrl is pressed
     * @param up     whether the player is scrolling up or down
     */
    void onInventoryScroll(ServerPlayer player, int slot, boolean shift, boolean up);

    /**
     * Called when a player shift or ctrl scrolls in the Grid View
     *
     * @param player player that is scrolling
     * @param id     UUID of the GridStack that the mouse is hovering over or null
     * @param shift  if true shift is pressed, if false ctrl is pressed
     * @param up     whether the player is scrolling up or down
     */
    void onGridScroll(ServerPlayer player, @Nullable UUID id, boolean shift, boolean up);
}
