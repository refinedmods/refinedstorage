package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageCraftingMonitorCancel extends MessageHandlerPlayerToServer<MessageCraftingMonitorCancel> implements IMessage {
    private int id;

    public MessageCraftingMonitorCancel() {
    }

    public MessageCraftingMonitorCancel(int id) {
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
    }

    @Override
    public void handle(MessageCraftingMonitorCancel message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerCraftingMonitor) {
            ((ContainerCraftingMonitor) player.openContainer).getCraftingMonitor().onCancelled(message.id);
        }
    }
}
