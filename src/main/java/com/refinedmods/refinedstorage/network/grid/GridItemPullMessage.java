package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.UUID;

public class GridItemPullMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_item_pull");

    private final UUID id;
    private final int flags;

    public GridItemPullMessage(UUID id, int flags) {
        this.id = id;
        this.flags = flags;
    }

    public static GridItemPullMessage decode(FriendlyByteBuf buf) {
        return new GridItemPullMessage(buf.readUUID(), buf.readInt());
    }

    public static void handle(GridItemPullMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            AbstractContainerMenu container = player.containerMenu;

            if (container instanceof GridContainerMenu) {
                IGrid grid = ((GridContainerMenu) container).getGrid();

                if (grid.getItemHandler() != null) {
                    grid.getItemHandler().onExtract((ServerPlayer) player, message.id, -1, message.flags);
                }
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(id);
        buf.writeInt(flags);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
