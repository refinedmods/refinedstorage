package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.container.GridContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

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

    public static GridItemGridScrollMessage decode(FriendlyByteBuf buf) {
        return new GridItemGridScrollMessage(buf.readBoolean() ? buf.readUUID() : null, buf.readBoolean(), buf.readBoolean());
    }

    public static void encode(GridItemGridScrollMessage message, FriendlyByteBuf buf) {
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
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof GridContainerMenu && ((GridContainerMenu) player.containerMenu).getGrid().getItemHandler() != null) {
                ((GridContainerMenu) player.containerMenu).getGrid().getItemHandler().onGridScroll(player, message.id, message.shift, message.up);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
