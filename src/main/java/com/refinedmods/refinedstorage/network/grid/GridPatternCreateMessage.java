package com.refinedmods.refinedstorage.network.grid;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class GridPatternCreateMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "grid_pattern_create");

    private final BlockPos pos;

    public GridPatternCreateMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static GridPatternCreateMessage decode(FriendlyByteBuf buf) {
        return new GridPatternCreateMessage(buf.readBlockPos());
    }

    public static void handle(GridPatternCreateMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            BlockEntity blockEntity = player.getCommandSenderWorld().getBlockEntity(message.pos);

            if (blockEntity instanceof GridBlockEntity &&
                ((GridBlockEntity) blockEntity).getNode().getGridType() == GridType.PATTERN) {
                ((GridBlockEntity) blockEntity).getNode().onCreatePattern();
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
