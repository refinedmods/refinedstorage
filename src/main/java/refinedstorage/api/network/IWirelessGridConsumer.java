package refinedstorage.api.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * Represents a player using a wireless grid.
 */
public interface IWirelessGridConsumer {
    /**
     * @return The hand this wireless grid is opened with
     */
    EnumHand getHand();

    /**
     * @return The player using the wireless grid
     */
    EntityPlayer getPlayer();

    /**
     * @return The wireless grid stack
     */
    ItemStack getStack();
}
