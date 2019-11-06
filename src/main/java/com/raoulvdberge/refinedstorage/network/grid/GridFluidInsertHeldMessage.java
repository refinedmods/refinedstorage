package com.raoulvdberge.refinedstorage.network.grid;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GridFluidInsertHeldMessage {
    public static GridFluidInsertHeldMessage decode(PacketBuffer buf) {
        return new GridFluidInsertHeldMessage();
    }

    public static void encode(GridFluidInsertHeldMessage message, PacketBuffer buf) {
    }

    public static void handle(GridFluidInsertHeldMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                Container container = player.openContainer;

                if (container instanceof GridContainer) {
                    IGrid grid = ((GridContainer) container).getGrid();

                    if (grid.getFluidHandler() != null) {
                        grid.getFluidHandler().onInsertHeldContainer(player);
                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
