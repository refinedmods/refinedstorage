package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GridCraftingStartRequestMessage {
    private final UUID id;
    private final int quantity;
    private final boolean fluids;

    public GridCraftingStartRequestMessage(UUID id, int quantity, boolean fluids) {
        this.id = id;
        this.quantity = quantity;
        this.fluids = fluids;
    }

    public static GridCraftingStartRequestMessage decode(FriendlyByteBuf buf) {
        return new GridCraftingStartRequestMessage(
            buf.readUUID(),
            buf.readInt(),
            buf.readBoolean()
        );
    }

    public static void encode(GridCraftingStartRequestMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.id);
        buf.writeInt(message.quantity);
        buf.writeBoolean(message.fluids);
    }

    public static void handle(GridCraftingStartRequestMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                AbstractContainerMenu container = player.containerMenu;

                if (container instanceof GridContainer) {
                    IGrid grid = ((GridContainer) container).getGrid();

                    if (message.fluids) {
                        if (grid.getFluidHandler() != null) {
                            grid.getFluidHandler().onCraftingRequested(player, message.id, message.quantity);
                        }
                    } else {
                        if (grid.getItemHandler() != null) {
                            grid.getItemHandler().onCraftingRequested(player, message.id, message.quantity);
                        }
                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
