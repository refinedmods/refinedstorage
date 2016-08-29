package refinedstorage.api.network.grid;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

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
     * @param hash   The hash of the item we're trying to extract, see {@link refinedstorage.api.network.NetworkUtils#getItemStackHashCode(ItemStack)}
     * @param flags  How we are extracting
     * @param player The player that is attempting the extraction
     */
    void onExtract(int hash, int flags, EntityPlayerMP player);

    /**
     * Called when a player tries to insert an item to the grid.
     *
     * @param player The player that is attempting the insert
     * @param stack  The item we're trying to insert
     * @return The remainder, or null if there is no remainder
     */
    @Nullable
    ItemStack onInsert(EntityPlayerMP player, ItemStack stack);

    /**
     * Called when a player is trying to insert an item that it is holding in their hand in the GUI.
     *
     * @param player The player that is attempting the insert
     * @param single If we are only inserting 1 item
     */
    void onInsertHeldItem(EntityPlayerMP player, boolean single);

    /**
     * Called when a player requested crafting for an item.
     *
     * @param hash     The hash of the item we're requesting crafting for, see {@link refinedstorage.api.network.NetworkUtils#getItemStackHashCode(ItemStack)}
     * @param quantity The amount of that item that has to be crafted
     */
    void onCraftingRequested(int hash, int quantity);

    /**
     * Called when a player wants to cancel a crafting task.
     *
     * @param id    The task ID, or -1 for all tasks
     * @param depth The child depth of this task to cancel
     */
    void onCraftingCancelRequested(int id, int depth);
}
