package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.tile.NetworkTransmitterTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class NetworkTransmitterContainer extends BaseContainer {
    public NetworkTransmitterContainer(NetworkTransmitterTile networkTransmitter, PlayerEntity player, int windowId) {
        super(RSContainers.NETWORK_TRANSMITTER, networkTransmitter, player, windowId);

        addSlot(new SlotItemHandler(networkTransmitter.getNode().getNetworkCard(), 0, 8, 20));

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, networkTransmitter.getNode().getNetworkCard());
    }
}
