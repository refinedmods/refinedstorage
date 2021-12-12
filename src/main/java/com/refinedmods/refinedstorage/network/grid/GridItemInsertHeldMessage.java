package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GridItemInsertHeldMessage {
    private final boolean single;

    public GridItemInsertHeldMessage(boolean single) {
        this.single = single;
    }

    public static GridItemInsertHeldMessage decode(PacketBuffer buf) {
        return new GridItemInsertHeldMessage(buf.readBoolean());
    }

    public static void encode(GridItemInsertHeldMessage message, PacketBuffer buf) {
        buf.writeBoolean(message.single);
    }

    public static void handle(GridItemInsertHeldMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                Container container = player.containerMenu;

                if (container instanceof GridContainer) {
                    IGrid grid = ((GridContainer) container).getGrid();

                    if (grid.getItemHandler() != null) {
                        grid.getItemHandler().onInsertHeldItem(player, message.single);
                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
