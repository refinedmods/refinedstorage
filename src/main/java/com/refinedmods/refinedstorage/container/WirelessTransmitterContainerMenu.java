package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.blockentity.WirelessTransmitterBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;

public class WirelessTransmitterContainerMenu extends BaseContainerMenu {
    public WirelessTransmitterContainerMenu(WirelessTransmitterBlockEntity wirelessTransmitter, Player player, int windowId) {
        super(RSContainerMenus.WIRELESS_TRANSMITTER.get(), wirelessTransmitter, player, windowId);

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(wirelessTransmitter.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.getInventory(), wirelessTransmitter.getNode().getUpgrades());
    }
}
