package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GridItemPullMessage {
    private final UUID id;
    private final int flags;

    public GridItemPullMessage(UUID id, int flags) {
        this.id = id;
        this.flags = flags;
    }

    public static GridItemPullMessage decode(PacketBuffer buf) {
        return new GridItemPullMessage(buf.readUUID(), buf.readInt());
    }

    public static void encode(GridItemPullMessage message, PacketBuffer buf) {
        buf.writeUUID(message.id);
        buf.writeInt(message.flags);
    }

    public static void handle(GridItemPullMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                Container container = player.containerMenu;

                if (container instanceof GridContainer) {
                    IGrid grid = ((GridContainer) container).getGrid();

                    if (grid.getItemHandler() != null) {
                        grid.getItemHandler().onExtract(player, message.id, -1, message.flags);
                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
