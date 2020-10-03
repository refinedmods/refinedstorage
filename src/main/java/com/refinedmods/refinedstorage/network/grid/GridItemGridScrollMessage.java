package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GridItemGridScrollMessage {
    private final UUID id;
    private final boolean shift;
    private final boolean up;
    private final boolean ctrl;

    public GridItemGridScrollMessage(UUID id, boolean shift, boolean ctrl, boolean up) {
        this.id = id;
        this.shift = shift;
        this.ctrl = ctrl;
        this.up = up;
    }

    public static GridItemGridScrollMessage decode(PacketBuffer buf) {
        return new GridItemGridScrollMessage(buf.readUniqueId(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
    }

    public static void encode(GridItemGridScrollMessage message, PacketBuffer buf) {
        buf.writeUniqueId(message.id);
        buf.writeBoolean(message.shift);
        buf.writeBoolean(message.ctrl);
        buf.writeBoolean(message.up);
    }

    public static void handle(GridItemGridScrollMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null && ctx.get().getSender().openContainer instanceof GridContainer) {
                ((GridContainer) ctx.get().getSender().openContainer).getGrid().getItemHandler().onGridScroll(ctx.get().getSender(), message.id, message.shift, message.ctrl, message.up);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
