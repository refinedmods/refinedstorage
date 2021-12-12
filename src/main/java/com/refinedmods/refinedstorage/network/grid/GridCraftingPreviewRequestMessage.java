package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public static GridCraftingPreviewRequestMessage decode(PacketBuffer buf) {
        return new GridCraftingPreviewRequestMessage(
            buf.readUUID(),
            buf.readInt(),
            buf.readBoolean(),
            buf.readBoolean()
        );
    }

    public static void encode(GridCraftingPreviewRequestMessage message, PacketBuffer buf) {
        buf.writeUUID(message.id);
        buf.writeInt(message.quantity);
        buf.writeBoolean(message.noPreview);
        buf.writeBoolean(message.fluids);
    }

    public static void handle(GridCraftingPreviewRequestMessage message, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                Container container = player.containerMenu;

                if (container instanceof GridContainer) {
                    IGrid grid = ((GridContainer) container).getGrid();

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
