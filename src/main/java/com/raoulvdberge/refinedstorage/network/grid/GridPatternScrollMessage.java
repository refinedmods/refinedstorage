package com.raoulvdberge.refinedstorage.network.grid;

import com.raoulvdberge.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GridPatternScrollMessage {
    private int offSet;

    public GridPatternScrollMessage(int offSet) {
        this.offSet = offSet;
    }

    public static GridPatternScrollMessage decode(PacketBuffer buf) {
        return new GridPatternScrollMessage(buf.readInt());
    }

    public static void encode(GridPatternScrollMessage message, PacketBuffer buf) {
        buf.writeInt(message.offSet);
    }

    public static void handle(GridPatternScrollMessage message, Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                Container container = player.openContainer;

                if (container instanceof GridContainer) {
                    ((GridContainer) container).updateContainerSlotPositions(message.offSet);
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
