package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class GridCraftingPreviewRequestMessage {
    private final UUID id;
    private final int quantity;
    private final boolean noPreview;
    private final boolean fluids;

    public GridCraftingPreviewRequestMessage(UUID id, int quantity, boolean noPreview, boolean fluids) {
        this.id = id;
        this.quantity = quantity;
        this.noPreview = noPreview;
        this.fluids = fluids;
    }

    public static GridCraftingPreviewRequestMessage decode(FriendlyByteBuf buf) {
        return new GridCraftingPreviewRequestMessage(
            buf.readUUID(),
            buf.readInt(),
            buf.readBoolean(),
            buf.readBoolean()
        );
    }

    public static void encode(GridCraftingPreviewRequestMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.id);
        buf.writeInt(message.quantity);
        buf.writeBoolean(message.noPreview);
        buf.writeBoolean(message.fluids);
    }

    public static void handle(GridCraftingPreviewRequestMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                AbstractContainerMenu container = player.containerMenu;

                if (container instanceof GridContainerMenu) {
                    IGrid grid = ((GridContainerMenu) container).getGrid();

                    if (message.fluids) {
                        if (grid.getFluidHandler() != null) {
                            grid.getFluidHandler().onCraftingPreviewRequested(player, message.id, message.quantity, message.noPreview);
                        }
                    } else {
                        if (grid.getItemHandler() != null) {
                            grid.getItemHandler().onCraftingPreviewRequested(player, message.id, message.quantity, message.noPreview);
                        }
                    }
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
