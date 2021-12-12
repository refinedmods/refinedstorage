package com.refinedmods.refinedstorage.network.craftingmonitor;

import com.refinedmods.refinedstorage.container.CraftingMonitorContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public class CraftingMonitorCancelMessage {
    @Nullable
    private final UUID taskId;

    public CraftingMonitorCancelMessage(@Nullable UUID taskId) {
        this.taskId = taskId;
    }

    public static CraftingMonitorCancelMessage decode(PacketBuffer buf) {
        return new CraftingMonitorCancelMessage(buf.readBoolean() ? buf.readUUID() : null);
    }

    public static void encode(CraftingMonitorCancelMessage message, PacketBuffer buf) {
        buf.writeBoolean(message.taskId != null);

        if (message.taskId != null) {
            buf.writeUUID(message.taskId);
        }
    }

    public static void handle(CraftingMonitorCancelMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

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
