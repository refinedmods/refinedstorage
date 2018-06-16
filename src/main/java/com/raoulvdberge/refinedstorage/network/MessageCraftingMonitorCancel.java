package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageCraftingMonitorCancel extends MessageHandlerPlayerToServer<MessageCraftingMonitorCancel> implements IMessage {
    private UUID taskId;

    public MessageCraftingMonitorCancel() {
    }

    public MessageCraftingMonitorCancel(UUID taskId) {
        this.taskId = taskId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            taskId = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(taskId != null);

        if (taskId != null) {
            ByteBufUtils.writeUTF8String(buf, taskId.toString());
        }
    }

    @Override
    public void handle(MessageCraftingMonitorCancel message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerCraftingMonitor) {
            ((ContainerCraftingMonitor) player.openContainer).getCraftingMonitor().onCancelled(player, message.taskId);
        }
    }
}
