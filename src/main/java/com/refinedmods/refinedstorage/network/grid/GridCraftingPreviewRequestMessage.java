package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainerMenu;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class GridCraftingPreviewRequestMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_crafting_preview_request");

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

    public static void handle(GridCraftingPreviewRequestMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            AbstractContainerMenu container = player.containerMenu;

            if (container instanceof GridContainerMenu) {
                IGrid grid = ((GridContainerMenu) container).getGrid();

                if (message.fluids) {
                    if (grid.getFluidHandler() != null) {
                        grid.getFluidHandler()
                            .onCraftingPreviewRequested((ServerPlayer) player, message.id, message.quantity,
                                message.noPreview);
                    }
                } else {
                    if (grid.getItemHandler() != null) {
                        grid.getItemHandler()
                            .onCraftingPreviewRequested((ServerPlayer) player, message.id, message.quantity,
                                message.noPreview);
                    }
                }
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(id);
        buf.writeInt(quantity);
        buf.writeBoolean(noPreview);
        buf.writeBoolean(fluids);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
