package com.refinedmods.refinedstorage.network;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.FilterContainerMenu;
import com.refinedmods.refinedstorage.item.FilterItem;
import com.refinedmods.refinedstorage.util.PacketBufferUtils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class FilterUpdateMessage implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "filter_update");

    private final int compare;
    private final int mode;
    private final boolean modFilter;
    private final String name;
    private final int type;

    public FilterUpdateMessage(int compare, int mode, boolean modFilter, String name, int type) {
        this.compare = compare;
        this.mode = mode;
        this.modFilter = modFilter;
        this.name = name;
        this.type = type;
    }

    public static FilterUpdateMessage decode(FriendlyByteBuf buf) {
        return new FilterUpdateMessage(
            buf.readInt(),
            buf.readInt(),
            buf.readBoolean(),
            PacketBufferUtils.readString(buf),
            buf.readInt()
        );
    }

    public static void handle(FilterUpdateMessage message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            FilterItem.setCompare(((FilterContainerMenu) player.containerMenu).getFilterItem(), message.compare);
            FilterItem.setMode(((FilterContainerMenu) player.containerMenu).getFilterItem(), message.mode);
            FilterItem.setModFilter(((FilterContainerMenu) player.containerMenu).getFilterItem(), message.modFilter);
            FilterItem.setName(((FilterContainerMenu) player.containerMenu).getFilterItem(), message.name);
            FilterItem.setType(((FilterContainerMenu) player.containerMenu).getFilterItem(), message.type);
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(compare);
        buf.writeInt(mode);
        buf.writeBoolean(modFilter);
        buf.writeUtf(name);
        buf.writeInt(type);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }
}
