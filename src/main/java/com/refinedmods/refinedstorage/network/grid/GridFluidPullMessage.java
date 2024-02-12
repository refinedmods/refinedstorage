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

public class GridFluidPullMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_fluid_pull");

    private final UUID id;
    private final boolean shift;

    public GridFluidPullMessage(UUID id, boolean shift) {
        this.id = id;
        this.shift = shift;
    }

    public static GridFluidPullMessage decode(FriendlyByteBuf buf) {
        return new GridFluidPullMessage(buf.readUUID(), buf.readBoolean());
    }

    public static void handle(GridFluidPullMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            AbstractContainerMenu container = player.containerMenu;

            if (container instanceof GridContainerMenu) {
                IGrid grid = ((GridContainerMenu) container).getGrid();

                if (grid.getFluidHandler() != null) {
                    grid.getFluidHandler().onExtract((ServerPlayer) player, message.id, message.shift);
                }
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(id);
        buf.writeBoolean(shift);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
