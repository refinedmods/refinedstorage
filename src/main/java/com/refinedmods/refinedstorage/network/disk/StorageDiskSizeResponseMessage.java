package com.refinedmods.refinedstorage.network.disk;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.StorageDiskSync;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class StorageDiskSizeResponseMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "storage_disk_size_response");

    private final UUID id;
    private final int stored;
    private final int capacity;

    public StorageDiskSizeResponseMessage(UUID id, int stored, int capacity) {
        this.id = id;
        this.stored = stored;
        this.capacity = capacity;
    }

    public static StorageDiskSizeResponseMessage decode(FriendlyByteBuf buf) {
        return new StorageDiskSizeResponseMessage(buf.readUUID(), buf.readInt(), buf.readInt());
    }

    public static void handle(StorageDiskSizeResponseMessage message, PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> ((StorageDiskSync) API.instance().getStorageDiskSync()).setData(
            message.id,
            new StorageDiskSyncData(message.stored, message.capacity)
        ));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(id);
        buf.writeInt(stored);
        buf.writeInt(capacity);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
