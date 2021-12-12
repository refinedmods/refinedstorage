package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.tile.SecurityManagerTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SecurityManagerUpdateMessage {
    private final BlockPos pos;
    private final Permission permission;
    private final boolean state;

    public SecurityManagerUpdateMessage(BlockPos pos, Permission permission, boolean state) {
        this.pos = pos;
        this.permission = permission;
        this.state = state;
    }

    public static SecurityManagerUpdateMessage decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();

        int id = buf.readInt();
        Permission permission = Permission.INSERT;

        for (Permission otherPermission : Permission.values()) {
            if (otherPermission.getId() == id) {
                permission = otherPermission;
                break;
            }
        }

        boolean state = buf.readBoolean();

        return new SecurityManagerUpdateMessage(pos, permission, state);
    }

    public static void encode(SecurityManagerUpdateMessage message, PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
        buf.writeInt(message.permission.getId());
        buf.writeBoolean(message.state);
    }

    public static void handle(SecurityManagerUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                TileEntity tile = player.getCommandSenderWorld().getBlockEntity(message.pos);

                if (tile instanceof SecurityManagerTile) {
                    ((SecurityManagerTile) tile).getNode().updatePermission(message.permission, message.state);
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
