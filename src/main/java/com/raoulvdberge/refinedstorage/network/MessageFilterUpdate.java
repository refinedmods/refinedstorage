package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.ContainerFilter;
import com.raoulvdberge.refinedstorage.item.ItemFilter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageFilterUpdate {
    private int compare;
    private int mode;
    private boolean modFilter;
    private String name;
    private int type;

    public MessageFilterUpdate(int compare, int mode, boolean modFilter, String name, int type) {
        this.compare = compare;
        this.mode = mode;
        this.modFilter = modFilter;
        this.name = name;
        this.type = type;
    }

    public static MessageFilterUpdate decode(PacketBuffer buf) {
        return new MessageFilterUpdate(
            buf.readInt(),
            buf.readInt(),
            buf.readBoolean(),
            buf.readString(),
            buf.readInt()
        );
    }

    public static void encode(MessageFilterUpdate message, PacketBuffer buf) {
        buf.writeInt(message.compare);
        buf.writeInt(message.mode);
        buf.writeBoolean(message.modFilter);
        buf.writeString(message.name);
        buf.writeInt(message.type);
    }

    public static void handle(MessageFilterUpdate message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();

            if (player != null && player.openContainer instanceof ContainerFilter) {
                ItemFilter.setCompare(((ContainerFilter) player.openContainer).getStack(), message.compare);
                ItemFilter.setMode(((ContainerFilter) player.openContainer).getStack(), message.mode);
                ItemFilter.setModFilter(((ContainerFilter) player.openContainer).getStack(), message.modFilter);
                ItemFilter.setName(((ContainerFilter) player.openContainer).getStack(), message.name);
                ItemFilter.setType(((ContainerFilter) player.openContainer).getStack(), message.type);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
