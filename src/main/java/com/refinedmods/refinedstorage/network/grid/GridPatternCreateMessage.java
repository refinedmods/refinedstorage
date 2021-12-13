package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
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
                BlockEntity blockEntity = player.getCommandSenderWorld().getBlockEntity(message.pos);

                if (blockEntity instanceof GridBlockEntity && ((GridBlockEntity) blockEntity).getNode().getGridType() == GridType.PATTERN) {
                    ((GridBlockEntity) blockEntity).getNode().onCreatePattern();
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
