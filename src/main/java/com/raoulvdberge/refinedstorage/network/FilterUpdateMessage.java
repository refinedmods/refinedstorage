package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.FilterContainer;
import com.raoulvdberge.refinedstorage.item.FilterItem;
import com.raoulvdberge.refinedstorage.util.PacketBufferUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class FilterUpdateMessage {
    private int compare;
    private int mode;
    private boolean modFilter;
    private String name;
    private int type;

    public FilterUpdateMessage(int compare, int mode, boolean modFilter, String name, int type) {
        this.compare = compare;
        this.mode = mode;
        this.modFilter = modFilter;
        this.name = name;
        this.type = type;
    }

    public static FilterUpdateMessage decode(PacketBuffer buf) {
        return new FilterUpdateMessage(
            buf.readInt(),
            buf.readInt(),
            buf.readBoolean(),
            PacketBufferUtils.readString(buf),
            buf.readInt()
        );
    }

    public static void encode(FilterUpdateMessage message, PacketBuffer buf) {
        buf.writeInt(message.compare);
        buf.writeInt(message.mode);
        buf.writeBoolean(message.modFilter);
        buf.writeString(message.name);
        buf.writeInt(message.type);
    }

    public static void handle(FilterUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity player = ctx.get().getSender();

        if (player != null && player.openContainer instanceof FilterContainer) {
            ctx.get().enqueueWork(() -> {
                FilterItem.setCompare(((FilterContainer) player.openContainer).getStack(), message.compare);
                FilterItem.setMode(((FilterContainer) player.openContainer).getStack(), message.mode);
                FilterItem.setModFilter(((FilterContainer) player.openContainer).getStack(), message.modFilter);
                FilterItem.setName(((FilterContainer) player.openContainer).getStack(), message.name);
                FilterItem.setType(((FilterContainer) player.openContainer).getStack(), message.type);
            });
        }

        ctx.get().setPacketHandled(true);
    }
}
