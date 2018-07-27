package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.TileWirelessTransmitter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerWirelessTransmitter extends ContainerBase {
    public ContainerWirelessTransmitter(TileWirelessTransmitter wirelessTransmitter, EntityPlayer player) {
        super(wirelessTransmitter, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(wirelessTransmitter.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, wirelessTransmitter.getNode().getUpgrades());
    }
}
