package com.refinedmods.refinedstorage.network.craftingmonitor;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.CraftingMonitorContainerMenu;

import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class CraftingMonitorCancelMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "crafting_monitor_cancel");

    @Nullable
    private final UUID taskId;

    public CraftingMonitorCancelMessage(@Nullable UUID taskId) {
        this.taskId = taskId;
    }

    public static CraftingMonitorCancelMessage decode(FriendlyByteBuf buf) {
        return new CraftingMonitorCancelMessage(buf.readBoolean() ? buf.readUUID() : null);
    }

    public static void handle(CraftingMonitorCancelMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            if (player.containerMenu instanceof CraftingMonitorContainerMenu) {
                ((CraftingMonitorContainerMenu) player.containerMenu).getCraftingMonitor().onCancelled(
                    (ServerPlayer) player,
                    message.taskId
                );
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(taskId != null);

        if (taskId != null) {
            buf.writeUUID(taskId);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
