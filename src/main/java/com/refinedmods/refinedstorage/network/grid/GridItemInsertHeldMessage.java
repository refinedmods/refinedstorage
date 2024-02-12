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

public class GridItemInsertHeldMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_item_insert_held");

    private final boolean single;

    public GridItemInsertHeldMessage(boolean single) {
        this.single = single;
    }

    public static GridItemInsertHeldMessage decode(FriendlyByteBuf buf) {
        return new GridItemInsertHeldMessage(buf.readBoolean());
    }

    public static void handle(GridItemInsertHeldMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            AbstractContainerMenu container = player.containerMenu;

            if (container instanceof GridContainerMenu) {
                IGrid grid = ((GridContainerMenu) container).getGrid();

                if (grid.getItemHandler() != null) {
                    grid.getItemHandler().onInsertHeldItem((ServerPlayer) player, message.single);
                }
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(single);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
