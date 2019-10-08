package com.raoulvdberge.refinedstorage.network.grid;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GridItemPullMessage {
    private int hash;
    private int flags;

    public GridItemPullMessage(int hash, int flags) {
        this.hash = hash;
        this.flags = flags;
    }

    public static GridItemPullMessage decode(PacketBuffer buf) {
        return new GridItemPullMessage(buf.readInt(), buf.readInt());
    }

    public static void encode(GridItemPullMessage message, PacketBuffer buf) {
        buf.writeInt(message.hash);
        buf.writeInt(message.flags);
    }

    public static void handle(GridItemPullMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        ctx.get().enqueueWork(() -> {
            if (player != null) {
                Container container = player.openContainer;

                if (container instanceof GridContainer) {
                    IGrid grid = ((GridContainer) container).getGrid();

                    if (grid.getItemHandler() != null) {
                        grid.getItemHandler().onExtract(player, message.hash, message.flags);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
