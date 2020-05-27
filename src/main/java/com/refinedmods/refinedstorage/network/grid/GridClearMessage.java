package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GridClearMessage {
    public static GridClearMessage decode(PacketBuffer buf) {
        return new GridClearMessage();
    }

    public static void encode(GridClearMessage message, PacketBuffer buf) {
        // NO OP
    }

    public static void handle(GridClearMessage message, Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                Container container = player.openContainer;

                if (container instanceof GridContainer) {
                    ((GridContainer) container).getGrid().onClear(player);
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
