package com.raoulvdberge.refinedstorage.api.network.grid.handler;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

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
     * Called when a player tries to extract an item from the grid.
     *
     * @param player the player that is attempting the extraction
     * @param id     the id of the item we're trying to extract, this id is the id from {@link com.raoulvdberge.refinedstorage.api.util.StackListEntry}
     * @param flags  how we are extracting, see the flags in {@link IItemGridHandler}
     */
    void onExtract(ServerPlayerEntity player, UUID id, int flags);

    /**
     * Called when a player tries to insert an item in the grid.
     *
     * @param player the player that is attempting the insert
     * @param stack  the item we're trying to insert
     * @return the remainder, or null if there is no remainder
     */
    @Nonnull
    ItemStack onInsert(ServerPlayerEntity player, ItemStack stack);

    /**
     * Called when a player is trying to insert an item that it is holding in their hand in the GUI.
     *
     * @param player the player that is attempting the insert
     * @param single true if we are only inserting a single item, false otherwise
     */
    void onInsertHeldItem(ServerPlayerEntity player, boolean single);

    /**
     * Called when the player shift clicks an item into the grid.
     *
     * @param player the player
     * @param stack  the stack
     * @return the remainder stack
     */
    // TODO Maybe remove?
    @Nonnull
    ItemStack onShiftClick(ServerPlayerEntity player, ItemStack stack);

    /**
     * Called when a player requests the crafting preview window to be opened.
     *
     * @param player    the player
     * @param id        the id of the item we're trying to extract, this id is the id from {@link com.raoulvdberge.refinedstorage.api.util.StackListEntry}
     * @param quantity  the amount of that item that we need a preview for
     * @param noPreview true if the crafting preview window shouldn't be shown, false otherwise
     */
    void onCraftingPreviewRequested(ServerPlayerEntity player, UUID id, int quantity, boolean noPreview);

    /**
     * Called when a player requested crafting for an item.
     *
     * @param player   the player that is requesting the crafting
     * @param id       the id of the item we're trying to extract, this id is the id from {@link com.raoulvdberge.refinedstorage.api.util.StackListEntry}
     * @param quantity the amount of the item that has to be crafted
     */
    void onCraftingRequested(ServerPlayerEntity player, UUID id, int quantity);

    /**
     * Called when a player wants to cancel a crafting task.
     *
     * @param player the player that requested the cancel
     * @param id     the task id, or null to cancel all tasks that are in the network currently
     */
    void onCraftingCancelRequested(ServerPlayerEntity player, @Nullable UUID id);
}
