package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class GridItemInventoryScrollMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_item_inventory_scroll");

    private final int slot;
    private final boolean shift;
    private final boolean up;

    public GridItemInventoryScrollMessage(int slot, boolean shift, boolean up) {
        this.slot = slot;
        this.shift = shift;
        this.up = up;
    }

    public static GridItemInventoryScrollMessage decode(FriendlyByteBuf buf) {
        return new GridItemInventoryScrollMessage(buf.readInt(), buf.readBoolean(), buf.readBoolean());
    }

    public static void handle(GridItemInventoryScrollMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            if (player.containerMenu instanceof GridContainerMenu && ((GridContainerMenu) player.containerMenu).getGrid().getItemHandler() != null) {
                ((GridContainerMenu) player.containerMenu).getGrid().getItemHandler().onInventoryScroll((ServerPlayer) player, message.slot, message.shift, message.up);
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        buf.writeBoolean(shift);
        buf.writeBoolean(up);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
