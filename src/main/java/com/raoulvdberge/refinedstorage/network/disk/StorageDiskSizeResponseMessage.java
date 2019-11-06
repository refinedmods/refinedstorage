package com.raoulvdberge.refinedstorage.network.disk;

import com.raoulvdberge.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.storage.disk.StorageDiskSync;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class StorageDiskSizeResponseMessage {
    private UUID id;
    private int stored;
    private int capacity;

    public StorageDiskSizeResponseMessage(UUID id, int stored, int capacity) {
        this.id = id;
        this.stored = stored;
        this.capacity = capacity;
    }

    public static void encode(StorageDiskSizeResponseMessage message, PacketBuffer buf) {
        buf.writeUniqueId(message.id);
        buf.writeInt(message.stored);
        buf.writeInt(message.capacity);
    }

    public static StorageDiskSizeResponseMessage decode(PacketBuffer buf) {
        return new StorageDiskSizeResponseMessage(buf.readUniqueId(), buf.readInt(), buf.readInt());
    }

    public static void handle(StorageDiskSizeResponseMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ((StorageDiskSync) API.instance().getStorageDiskSync()).setData(message.id, new StorageDiskSyncData(message.stored, message.capacity)));
        ctx.get().setPacketHandled(true);
    }
}
