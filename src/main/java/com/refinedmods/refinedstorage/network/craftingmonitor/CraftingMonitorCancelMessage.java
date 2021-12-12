package com.refinedmods.refinedstorage.network.craftingmonitor;

import com.refinedmods.refinedstorage.container.CraftingMonitorContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public class CraftingMonitorCancelMessage {
    @Nullable
    private final UUID taskId;

    public CraftingMonitorCancelMessage(@Nullable UUID taskId) {
        this.taskId = taskId;
    }

    public static CraftingMonitorCancelMessage decode(FriendlyByteBuf buf) {
        return new CraftingMonitorCancelMessage(buf.readBoolean() ? buf.readUUID() : null);
    }

    public static void encode(CraftingMonitorCancelMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.taskId != null);

        if (message.taskId != null) {
            buf.writeUUID(message.taskId);
        }
    }

    public static void handle(CraftingMonitorCancelMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                if (player.containerMenu instanceof CraftingMonitorContainer) {
                    ((CraftingMonitorContainer) player.containerMenu).getCraftingMonitor().onCancelled(player, message.taskId);
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
