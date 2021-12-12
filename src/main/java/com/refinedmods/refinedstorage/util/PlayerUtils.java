package com.refinedmods.refinedstorage.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SSetSlotPacket;

public class PlayerUtils {


    public static void updateHeldItems(ServerPlayerEntity player) {
        int mainHandSlot = player.inventory.selected;
        int offHandSlot = 40; //TODO: @Volatile In 1.17 there is a global variable for this

        //See ClientPlayNetHandler#HandleSetSlot for the awful vanilla code that makes this necessary
        // -2 as that directly sets the inventory slots
        player.connection.send(new SSetSlotPacket(-2, mainHandSlot, player.inventory.getItem(mainHandSlot)));
        player.connection.send(new SSetSlotPacket(-2, offHandSlot, player.inventory.getItem(offHandSlot)));
    }
}
