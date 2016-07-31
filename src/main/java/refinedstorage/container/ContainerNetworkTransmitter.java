package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.tile.TileNetworkTransmitter;

public class ContainerNetworkTransmitter extends ContainerBase {
    public ContainerNetworkTransmitter(EntityPlayer player, TileNetworkTransmitter networkTransmitter) {
        super(player);

        addSlotToContainer(new SlotItemHandler(networkTransmitter.getNetworkCard(), 0, 8, 20));

        addPlayerInventory(8, 55);
    }
}
