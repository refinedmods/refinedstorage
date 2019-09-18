package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.FilterContainer;
import com.raoulvdberge.refinedstorage.item.FilterItem;
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

            if (player != null && player.openContainer instanceof FilterContainer) {
                FilterItem.setCompare(((FilterContainer) player.openContainer).getStack(), message.compare);
                FilterItem.setMode(((FilterContainer) player.openContainer).getStack(), message.mode);
                FilterItem.setModFilter(((FilterContainer) player.openContainer).getStack(), message.modFilter);
                FilterItem.setName(((FilterContainer) player.openContainer).getStack(), message.name);
                FilterItem.setType(((FilterContainer) player.openContainer).getStack(), message.type);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
