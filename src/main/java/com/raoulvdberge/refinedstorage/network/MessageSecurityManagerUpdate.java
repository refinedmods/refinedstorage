package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.Permission;
import com.raoulvdberge.refinedstorage.tile.TileSecurityManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageSecurityManagerUpdate extends MessageHandlerPlayerToServer<MessageSecurityManagerUpdate> implements IMessage {
    private int x;
    private int y;
    private int z;
    private Permission permission;
    private boolean state;

    public MessageSecurityManagerUpdate() {
    }

    public MessageSecurityManagerUpdate(TileSecurityManager securityManager, Permission permission, boolean state) {
        this.x = securityManager.getPos().getX();
        this.y = securityManager.getPos().getY();
        this.z = securityManager.getPos().getZ();
        this.permission = permission;
        this.state = state;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();

        int id = buf.readInt();

        permission = Permission.INSERT;
        state = buf.readBoolean();

        for (Permission otherPermission : Permission.values()) {
            if (otherPermission.getId() == id) {
                permission = otherPermission;

                break;
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(permission.getId());
        buf.writeBoolean(state);
    }

    @Override
    protected void handle(MessageSecurityManagerUpdate message, EntityPlayerMP player) {
        TileEntity tile = player.getEntityWorld().getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof TileSecurityManager) {
            ((TileSecurityManager) tile).updatePermission(message.permission, message.state);
        }
    }

}
