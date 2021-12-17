package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GridPatternCreateMessage {
    private final BlockPos pos;

    public GridPatternCreateMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static GridPatternCreateMessage decode(FriendlyByteBuf buf) {
        return new GridPatternCreateMessage(buf.readBlockPos());
    }

    public static void encode(GridPatternCreateMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
    }

    public static void handle(GridPatternCreateMessage message, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                if (WorldUtils.getLoadedBlockEntity(player.getCommandSenderWorld(), message.pos) instanceof GridBlockEntity gridBlockEntity
                    && gridBlockEntity.getNode().getGridType() == GridType.PATTERN) {
                    gridBlockEntity.getNode().onCreatePattern();
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
