package com.refinedmods.refinedstorage.network.disk;

import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.StorageDiskSync;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class StorageDiskSizeResponseMessage {
    private final UUID id;
    private final int stored;
    private final int capacity;

    public StorageDiskSizeResponseMessage(UUID id, int stored, int capacity) {
        this.id = id;
        this.stored = stored;
        this.capacity = capacity;
    }

    public static void encode(StorageDiskSizeResponseMessage message, PacketBuffer buf) {
        buf.writeUUID(message.id);
        buf.writeInt(message.stored);
        buf.writeInt(message.capacity);
    }

    public static StorageDiskSizeResponseMessage decode(PacketBuffer buf) {
        return new StorageDiskSizeResponseMessage(buf.readUUID(), buf.readInt(), buf.readInt());
    }

    public static void handle(StorageDiskSizeResponseMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ((StorageDiskSync) API.instance().getStorageDiskSync()).setData(message.id, new StorageDiskSyncData(message.stored, message.capacity)));
        ctx.get().setPacketHandled(true);
    }
}
