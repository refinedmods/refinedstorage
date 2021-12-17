package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.blockentity.SecurityManagerBlockEntity;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

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

    public static SecurityManagerUpdateMessage decode(FriendlyByteBuf buf) {
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

    public static void encode(SecurityManagerUpdateMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.pos);
        buf.writeInt(message.permission.getId());
        buf.writeBoolean(message.state);
    }

    public static void handle(SecurityManagerUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();

        if (player != null) {
            ctx.get().enqueueWork(() -> {
                if (WorldUtils.getLoadedBlockEntity(player.getCommandSenderWorld(), message.pos) instanceof SecurityManagerBlockEntity securityManagerBlockEntity) {
                    securityManagerBlockEntity.getNode().updatePermission(message.permission, message.state);
                }
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
