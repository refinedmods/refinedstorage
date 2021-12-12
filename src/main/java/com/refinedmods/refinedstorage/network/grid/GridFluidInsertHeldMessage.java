package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GridFluidInsertHeldMessage {
    public static void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                Container container = player.containerMenu;

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
