package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public class GridItemGridScrollMessage {
    private final UUID id;
    private final boolean shift;
    private final boolean up;

    public GridItemGridScrollMessage(@Nullable UUID id, boolean shift, boolean up) {
        this.id = id;
        this.shift = shift;
        this.up = up;
    }

    public static GridItemGridScrollMessage decode(PacketBuffer buf) {
        return new GridItemGridScrollMessage(buf.readBoolean() ? buf.readUUID() : null, buf.readBoolean(), buf.readBoolean());
    }

    public static void encode(GridItemGridScrollMessage message, PacketBuffer buf) {
        boolean hasId = message.id != null;
        buf.writeBoolean(hasId);
        if (hasId) {
            buf.writeUUID(message.id);
        }

        buf.writeBoolean(message.shift);
        buf.writeBoolean(message.up);
    }

    public static void handle(GridItemGridScrollMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof GridContainer && ((GridContainer) player.containerMenu).getGrid().getItemHandler() != null) {
                ((GridContainer) player.containerMenu).getGrid().getItemHandler().onGridScroll(player, message.id, message.shift, message.up);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
