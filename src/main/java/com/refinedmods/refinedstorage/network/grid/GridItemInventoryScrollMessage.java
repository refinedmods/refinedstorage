package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.container.GridContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GridItemInventoryScrollMessage {
    private final int slot;
    private final boolean shift;
    private final boolean up;

    public GridItemInventoryScrollMessage(int slot, boolean shift, boolean up) {
        this.slot = slot;
        this.shift = shift;
        this.up = up;
    }

    public static GridItemInventoryScrollMessage decode(FriendlyByteBuf buf) {
        return new GridItemInventoryScrollMessage(buf.readInt(), buf.readBoolean(), buf.readBoolean());
    }

    public static void encode(GridItemInventoryScrollMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.slot);
        buf.writeBoolean(message.shift);
        buf.writeBoolean(message.up);
    }

    public static void handle(GridItemInventoryScrollMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof GridContainerMenu && ((GridContainerMenu) player.containerMenu).getGrid().getItemHandler() != null) {
                ((GridContainerMenu) player.containerMenu).getGrid().getItemHandler().onInventoryScroll(player, message.slot, message.shift, message.up);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
