package refinedstorage.api.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Represents a player using a wireless grid.
 */
public interface IWirelessGridConsumer {
    /**
     * @return The player using the wireless grid
     */
    EntityPlayer getPlayer();

    /**
     * @return The wireless grid stack
     */
    ItemStack getStack();
}
