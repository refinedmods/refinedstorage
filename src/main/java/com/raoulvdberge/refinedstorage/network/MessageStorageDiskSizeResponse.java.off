package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskSync;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskSyncData;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageStorageDiskSizeResponse implements IMessage, IMessageHandler<MessageStorageDiskSizeResponse, IMessage> {
    private UUID id;
    private int stored;
    private int capacity;

    public MessageStorageDiskSizeResponse() {
    }

    public MessageStorageDiskSizeResponse(UUID id, int stored, int capacity) {
        this.id = id;
        this.stored = stored;
        this.capacity = capacity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        stored = buf.readInt();
        capacity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, id.toString());
        buf.writeInt(stored);
        buf.writeInt(capacity);
    }

    @Override
    public IMessage onMessage(MessageStorageDiskSizeResponse message, MessageContext ctx) {
        ((StorageDiskSync) API.instance().getStorageDiskSync()).setData(message.id, new StorageDiskSyncData(message.stored, message.capacity));

        return null;
    }
}
