package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskSync;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskSyncData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MessageStorageDiskSizeResponse {
    private UUID id;
    private int stored;
    private int capacity;

    public MessageStorageDiskSizeResponse(UUID id, int stored, int capacity) {
        this.id = id;
        this.stored = stored;
        this.capacity = capacity;
    }

    public static void encode(MessageStorageDiskSizeResponse message, PacketBuffer buf) {
        buf.writeUniqueId(message.id);
        buf.writeInt(message.stored);
        buf.writeInt(message.capacity);
    }

    public static MessageStorageDiskSizeResponse decode(PacketBuffer buf) {
        return new MessageStorageDiskSizeResponse(buf.readUniqueId(), buf.readInt(), buf.readInt());
    }

    public static void handle(MessageStorageDiskSizeResponse message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ((StorageDiskSync) API.instance().getStorageDiskSync()).setData(message.id, new StorageDiskSyncData(message.stored, message.capacity)));
        ctx.get().setPacketHandled(true);
    }
}
