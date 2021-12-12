package com.refinedmods.refinedstorage.util;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

public class PlayerUtils {
    public static void updateHeldItems(ServerPlayer player) {
        int mainHandSlot = player.getInventory().selected;
        int offHandSlot = Inventory.SLOT_OFFHAND;

        //See ClientPlayNetHandler#HandleSetSlot for the awful vanilla code that makes this necessary
        // -2 as that directly sets the inventory slots
        player.connection.send(new ClientboundContainerSetSlotPacket(-2, player.containerMenu.incrementStateId(), mainHandSlot, player.getInventory().getItem(mainHandSlot)));
        player.connection.send(new ClientboundContainerSetSlotPacket(-2, player.containerMenu.incrementStateId(), offHandSlot, player.getInventory().getItem(offHandSlot)));
    }
}
