package refinedstorage.apiimpl.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import refinedstorage.api.network.IWirelessGridConsumer;

public class WirelessGridConsumer implements IWirelessGridConsumer {
    private EntityPlayer player;
    private ItemStack stack;

    public WirelessGridConsumer(EntityPlayer player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
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