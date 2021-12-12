package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GridFluidPullMessage {
    private final UUID id;
    private final boolean shift;

    public GridFluidPullMessage(UUID id, boolean shift) {
        this.id = id;
        this.shift = shift;
    }

    public static GridFluidPullMessage decode(PacketBuffer buf) {
        return new GridFluidPullMessage(buf.readUUID(), buf.readBoolean());
    }

    public static void encode(GridFluidPullMessage message, PacketBuffer buf) {
        buf.writeUUID(message.id);
        buf.writeBoolean(message.shift);
    }

    public static void handle(GridFluidPullMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                Container container = player.containerMenu;

                if (container instanceof GridContainer) {
                    IGrid grid = ((GridContainer) container).getGrid();

                    if (grid.getFluidHandler() != null) {
                        grid.getFluidHandler().onExtract(player, message.id, message.shift);
                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
