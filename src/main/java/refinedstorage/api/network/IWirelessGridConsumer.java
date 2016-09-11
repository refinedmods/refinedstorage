package refinedstorage.api.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Represents a player using a wireless grid.
 */
public interface IWirelessGridConsumer {
    /**
     * @return the player using the wireless grid
     */
    EntityPlayer getPlayer();

    /**
     * @return the wireless grid stack
     */
    ItemStack getStack();
}
