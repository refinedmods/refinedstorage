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
     * @param hash   the hash of the item we're trying to extract, see {@link refinedstorage.api.IRSAPI#getItemStackHashCode(ItemStack)}
     * @param flags  how we are extracting
     * @param player the player that is attempting the extraction
     */
    void onExtract(int hash, int flags, EntityPlayerMP player);

    /**
     * Called when a player tries to insert an item in the grid.
     *
     * @param player the player that is attempting the insert
     * @param stack  the item we're trying to insert
     * @return the remainder, or null if there is no remainder
     */
    @Nullable
    ItemStack onInsert(EntityPlayerMP player, ItemStack stack);

    /**
     * Called when a player is trying to insert an item that it is holding in their hand in the GUI.
     *
     * @param player the player that is attempting the insert
     * @param single true if we are only inserting a single item, false otherwise
     */
    void onInsertHeldItem(EntityPlayerMP player, boolean single);

    /**
     * Called when a player requests the crafting preview window to be opened.
     *
     * @param hash     the hash of the item we want a preview for, see {@link refinedstorage.api.IRSAPI#getItemStackHashCode(ItemStack)}
     * @param quantity the amount of that item that we need a preview for
     */
    void onCraftingPreviewRequested(EntityPlayerMP player, int hash, int quantity);

    /**
     * Called when a player requested crafting for an item.
     *
     * @param hash     the hash of the item we're requesting crafting for, see {@link refinedstorage.api.IRSAPI#getItemStackHashCode(ItemStack)}
     * @param quantity the amount of that item that has to be crafted
     */
    void onCraftingRequested(int hash, int quantity);

    /**
     * Called when a player wants to cancel a crafting task.
     *
     * @param id the task id, or -1 to cancel all tasks
     */
    void onCraftingCancelRequested(int id);
}
