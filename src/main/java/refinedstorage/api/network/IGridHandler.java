package refinedstorage.api.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Defines the behaviour of grids.
 */
public interface IGridHandler {
    /**
     * Called when a player tries to pull an item from the grid.
     *
     * @param stack  The item we're trying to pull
     * @param flags  How we are pulling, see {@link GridPullFlags}
     * @param player The player that is attempting the pull
     */
    void onPull(ItemStack stack, int flags, EntityPlayerMP player);

    /**
     * Called when a player tries to push to the grid.
     *
     * @param stack The item we're trying to push
     * @return The remainder, or null if there is no remainder
     */
    @Nullable
    ItemStack onPush(ItemStack stack);

    /**
     * Called when a player is trying to push an item that it is holding in their hand in the GUI.
     *
     * @param single If we are only pushing 1 item
     * @param player The player that is attempting the push
     */
    void onHeldItemPush(boolean single, EntityPlayerMP player);

    /**
     * Called when a player requested crafting for an item.
     *
     * @param stack    The item we're requesting crafting for
     * @param quantity The amount of that item that has to be crafted
     */
    void onCraftingRequested(ItemStack stack, int quantity);

    /**
     * Called when a player wants to cancel a crafting task.
     *
     * @param id The task ID, or -1 for all tasks
     */
    void onCraftingCancelRequested(int id);
}
