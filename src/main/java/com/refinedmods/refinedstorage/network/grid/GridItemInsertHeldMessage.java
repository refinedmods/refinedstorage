package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GridItemInsertHeldMessage {
    private final boolean single;

    public GridItemInsertHeldMessage(boolean single) {
        this.single = single;
    }

    public static GridItemInsertHeldMessage decode(FriendlyByteBuf buf) {
        return new GridItemInsertHeldMessage(buf.readBoolean());
    }

    public static void encode(GridItemInsertHeldMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.single);
    }

    public static void handle(GridItemInsertHeldMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                AbstractContainerMenu container = player.containerMenu;

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
