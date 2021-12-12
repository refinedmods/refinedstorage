package com.refinedmods.refinedstorage.network.disk;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class StorageDiskSizeRequestMessage {
    private final UUID id;

    public StorageDiskSizeRequestMessage(UUID id) {
        this.id = id;
    }

    public static StorageDiskSizeRequestMessage decode(PacketBuffer buf) {
        return new StorageDiskSizeRequestMessage(buf.readUUID());
    }

    public static void encode(StorageDiskSizeRequestMessage message, PacketBuffer buf) {
        buf.writeUUID(message.id);
    }

    public static void handle(StorageDiskSizeRequestMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            IStorageDisk disk = API.instance().getStorageDiskManager(ctx.get().getSender().getLevel()).get(message.id);

            if (disk != null) {
                RS.NETWORK_HANDLER.sendTo(ctx.get().getSender(), new StorageDiskSizeResponseMessage(message.id, disk.getStored(), disk.getCapacity()));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
