package refinedstorage.apiimpl.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import refinedstorage.api.network.IWirelessGridConsumer;

public class WirelessGridConsumer implements IWirelessGridConsumer {
    private EntityPlayer player;
    private EnumHand hand;
    private ItemStack stack;

    public WirelessGridConsumer(EntityPlayer player, EnumHand hand, ItemStack stack) {
        this.player = player;
        this.hand = hand;
        this.stack = stack;
    }

    @Override
    public EnumHand getHand() {
        return hand;
    }

    @Override
    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }
}