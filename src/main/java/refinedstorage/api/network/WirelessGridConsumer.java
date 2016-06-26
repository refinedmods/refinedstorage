package refinedstorage.api.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * Represents a player using a wireless grid.
 */
public class WirelessGridConsumer {
    private EntityPlayer player;
    private EnumHand hand;
    private ItemStack wirelessGrid;

    /**
     * @param player       The player using this wireless grid
     * @param hand         The hand that this wireless grid is in
     * @param wirelessGrid The wireless grid {@link ItemStack} in the player's inventory
     */
    public WirelessGridConsumer(EntityPlayer player, EnumHand hand, ItemStack wirelessGrid) {
        this.player = player;
        this.hand = hand;
        this.wirelessGrid = wirelessGrid;
    }

    /**
     * @return The wireless grid {@link ItemStack}
     */
    public ItemStack getWirelessGrid() {
        return wirelessGrid;
    }

    /**
     * @return The hand this wireless grid is in
     */
    public EnumHand getHand() {
        return hand;
    }

    /**
     * @return The player using the wireless grid
     */
    public EntityPlayer getPlayer() {
        return player;
    }
}