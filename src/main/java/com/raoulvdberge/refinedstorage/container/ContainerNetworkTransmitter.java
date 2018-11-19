package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.TileNetworkTransmitter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerNetworkTransmitter extends ContainerBase {
    public ContainerNetworkTransmitter(TileNetworkTransmitter networkTransmitter, EntityPlayer player) {
        super(networkTransmitter, player);

        addSlotToContainer(new SlotItemHandler(networkTransmitter.getNode().getNetworkCard(), 0, 8, 20));

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, networkTransmitter.getNode().getNetworkCard());
    }
}
