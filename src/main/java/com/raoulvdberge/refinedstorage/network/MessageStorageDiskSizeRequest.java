package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MessageStorageDiskSizeRequest {
    private UUID id;

    public MessageStorageDiskSizeRequest(UUID id) {
        this.id = id;
    }

    public static MessageStorageDiskSizeRequest decode(PacketBuffer buf) {
        return new MessageStorageDiskSizeRequest(buf.readUniqueId());
    }

    public static void encode(MessageStorageDiskSizeRequest message, PacketBuffer buf) {
        buf.writeUniqueId(message.id);
    }

    public static void handle(MessageStorageDiskSizeRequest message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            IStorageDisk disk = API.instance().getStorageDiskManager(ctx.get().getSender().getServerWorld()).get(message.id);

            if (disk != null) {
                RS.NETWORK_HANDLER.sendTo(ctx.get().getSender(), new MessageStorageDiskSizeResponse(message.id, disk.getStored(), disk.getCapacity()));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
