package com.raoulvdberge.refinedstorage.network.grid;

import com.raoulvdberge.refinedstorage.api.network.grid.GridType;
import com.raoulvdberge.refinedstorage.tile.grid.GridTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GridPatternCreateMessage {
    private BlockPos pos;

    public GridPatternCreateMessage(BlockPos pos) {
        this.pos = pos;
    }

    public static GridPatternCreateMessage decode(PacketBuffer buf) {
        return new GridPatternCreateMessage(buf.readBlockPos());
    }

    public static void encode(GridPatternCreateMessage message, PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
    }

    public static void handle(GridPatternCreateMessage message, Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                TileEntity tile = player.getEntityWorld().getTileEntity(message.pos);

                if (tile instanceof GridTile && ((GridTile) tile).getNode().getGridType() == GridType.PATTERN) {
                    ((GridTile) tile).getNode().onCreatePattern();
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
