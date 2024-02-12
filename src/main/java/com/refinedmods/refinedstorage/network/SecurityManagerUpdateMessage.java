package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.blockentity.SecurityManagerBlockEntity;
import com.refinedmods.refinedstorage.container.SecurityManagerContainerMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SecurityManagerUpdateMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "security_manager_update");

    private final Permission permission;
    private final boolean state;

    public SecurityManagerUpdateMessage(Permission permission, boolean state) {
        this.permission = permission;
        this.state = state;
    }

    public static SecurityManagerUpdateMessage decode(FriendlyByteBuf buf) {
        int id = buf.readInt();
        Permission permission = Permission.INSERT;

        for (Permission otherPermission : Permission.values()) {
            if (otherPermission.getId() == id) {
                permission = otherPermission;
                break;
            }
        }

        boolean state = buf.readBoolean();

        return new SecurityManagerUpdateMessage(permission, state);
    }

    public static void handle(SecurityManagerUpdateMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            if (player.containerMenu instanceof SecurityManagerContainerMenu securityManagerContainerMenu) {
                securityManagerContainerMenu.updatePermission(message.permission, message.state);
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(permission.getId());
        buf.writeBoolean(state);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
