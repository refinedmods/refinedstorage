package refinedstorage.api.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public interface IWirelessGridHandler {
    void update();

    boolean onOpen(EntityPlayer player, EnumHand hand);

    void onClose(EntityPlayer player);

    void drainEnergy(EntityPlayer player, int energy);

    WirelessGridConsumer getConsumer(EntityPlayer player);
}
