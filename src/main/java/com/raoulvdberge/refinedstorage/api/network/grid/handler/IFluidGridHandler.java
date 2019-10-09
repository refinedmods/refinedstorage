package com.raoulvdberge.refinedstorage.api.network.grid.handler;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Defines the behavior of fluid grids.
 */
public interface IFluidGridHandler {
    /**
     * Called when a player tries to extract a fluid from the grid.
     *
     * @param player the player that is attempting the extraction
     * @param id     the id of the fluid we're trying to extract, this id is the id from {@link com.raoulvdberge.refinedstorage.api.util.StackListEntry}
     * @param shift  true if shift click was used, false otherwise
     */
    void onExtract(ServerPlayerEntity player, UUID id, boolean shift);

    /**
     * Called when a player tries to insert fluids in the grid.
     *
     * @param player    the player
     * @param container a stack with a fluid container we're trying to insert
     * @return the remainder, or null if there is no remainder
     */
    @Nullable
    ItemStack onInsert(ServerPlayerEntity player, ItemStack container);

    /**
     * Called when a player is trying to insert a fluid that it is holding in their hand in the GUI.
     *
     * @param player the player that is attempting the insert
     */
    void onInsertHeldContainer(ServerPlayerEntity player);

    /**
     * Called when the player shift clicks a container into the grid.
     *
     * @param player    the player
     * @param container the container
     * @return the remainder container
     */
    ItemStack onShiftClick(ServerPlayerEntity player, ItemStack container);

    /**
     * Called when a player requests the crafting preview window to be opened.
     *
     * @param player    the player
     * @param id        the id of the fluid we're trying to extract, this id is the id from {@link com.raoulvdberge.refinedstorage.api.util.StackListEntry}
     * @param quantity  the amount of that item that we need a preview for
     * @param noPreview true if the crafting preview window shouldn't be shown, false otherwise
     */
    void onCraftingPreviewRequested(ServerPlayerEntity player, UUID id, int quantity, boolean noPreview);

    /**
     * Called when a player requested crafting for an item.
     *
     * @param player   the player that is requesting the crafting
     * @param id       the id of the fluid we're trying to extract, this id is the id from {@link com.raoulvdberge.refinedstorage.api.util.StackListEntry}
     * @param quantity the amount of the item that has to be crafted
     */
    void onCraftingRequested(ServerPlayerEntity player, UUID id, int quantity);
}
