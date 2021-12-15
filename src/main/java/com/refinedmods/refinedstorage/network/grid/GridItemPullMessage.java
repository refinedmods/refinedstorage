package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GridItemPullMessage {
    private final UUID id;
    private final int flags;

    public GridItemPullMessage(UUID id, int flags) {
        this.id = id;
        this.flags = flags;
    }

    public static GridItemPullMessage decode(FriendlyByteBuf buf) {
        return new GridItemPullMessage(buf.readUUID(), buf.readInt());
    }

    public static void encode(GridItemPullMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.id);
        buf.writeInt(message.flags);
    }

    public static void handle(GridItemPullMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                AbstractContainerMenu container = player.containerMenu;

                if (container instanceof GridContainerMenu) {
                    IGrid grid = ((GridContainerMenu) container).getGrid();

                    if (grid.getItemHandler() != null) {
                        grid.getItemHandler().onExtract(player, message.id, -1, message.flags);
                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
