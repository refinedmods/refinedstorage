package com.refinedmods.refinedstorage.network.disk;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.UUID;

public class StorageDiskSizeRequestMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "storage_disk_size_request");

    private final UUID id;

    public StorageDiskSizeRequestMessage(UUID id) {
        this.id = id;
    }

    public static StorageDiskSizeRequestMessage decode(FriendlyByteBuf buf) {
        return new StorageDiskSizeRequestMessage(buf.readUUID());
    }

    public static void handle(StorageDiskSizeRequestMessage message, PlayPayloadContext context) {
        context.player().ifPresent(player -> context.workHandler().submitAsync(() -> {
            IStorageDisk disk = API.instance().getStorageDiskManager(((ServerPlayer) player).serverLevel()).get(message.id);

            if (disk != null) {
                RS.NETWORK_HANDLER.sendTo((ServerPlayer) player, new StorageDiskSizeResponseMessage(message.id, disk.getStored(), disk.getCapacity()));
            }
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(id);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
