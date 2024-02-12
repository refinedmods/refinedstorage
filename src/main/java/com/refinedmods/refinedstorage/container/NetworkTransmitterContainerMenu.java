package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.blockentity.NetworkTransmitterBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.SlotItemHandler;

public class NetworkTransmitterContainerMenu extends BaseContainerMenu {
    public NetworkTransmitterContainerMenu(NetworkTransmitterBlockEntity networkTransmitter, Player player, int windowId) {
        super(RSContainerMenus.NETWORK_TRANSMITTER.get(), networkTransmitter, player, windowId);

        addSlot(new SlotItemHandler(networkTransmitter.getNode().getNetworkCard(), 0, 8, 20));

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.getInventory(), networkTransmitter.getNode().getNetworkCard());
    }
}
