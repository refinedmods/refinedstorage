package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import javax.annotation.Nullable;
import java.util.UUID;

public class GridItemGridScrollMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_item_grid_scroll");

    private final UUID id;
    private final boolean shift;
    private final boolean up;

    public GridItemGridScrollMessage(@Nullable UUID id, boolean shift, boolean up) {
        this.id = id;
        this.shift = shift;
        this.up = up;
    }

    public static GridItemGridScrollMessage decode(FriendlyByteBuf buf) {
        return new GridItemGridScrollMessage(buf.readBoolean() ? buf.readUUID() : null, buf.readBoolean(), buf.readBoolean());
    }

    public static void handle(GridItemGridScrollMessage message, PlayPayloadContext ctx) {
       ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            if (player.containerMenu instanceof GridContainerMenu && ((GridContainerMenu) player.containerMenu).getGrid().getItemHandler() != null) {
                ((GridContainerMenu) player.containerMenu).getGrid().getItemHandler().onGridScroll((ServerPlayer) player, message.id, message.shift, message.up);
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        boolean hasId = id != null;
        buf.writeBoolean(hasId);
        if (hasId) {
            buf.writeUUID(id);
        }

        buf.writeBoolean(shift);
        buf.writeBoolean(up);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
