package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerNetworkReceiver extends ContainerBase {
    public ContainerNetworkReceiver(EntityPlayer player) {
        super(player);

        addPlayerInventory(8, 50);
    }
}
