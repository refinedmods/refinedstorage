package refinedstorage.api.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class WirelessGridConsumer {
    private EntityPlayer player;
    private EnumHand hand;
    private ItemStack wirelessGrid;

    public WirelessGridConsumer(EntityPlayer player, EnumHand hand, ItemStack wirelessGrid) {
        this.player = player;
        this.hand = hand;
        this.wirelessGrid = wirelessGrid;
    }

    public ItemStack getWirelessGrid() {
        return wirelessGrid;
    }

    public EnumHand getHand() {
        return hand;
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}