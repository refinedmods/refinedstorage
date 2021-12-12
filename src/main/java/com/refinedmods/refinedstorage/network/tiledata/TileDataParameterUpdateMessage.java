package com.refinedmods.refinedstorage.network.tiledata;

import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class TileDataParameterUpdateMessage {
    private final TileDataParameter parameter;
    private final Object value;

    public TileDataParameterUpdateMessage(TileDataParameter parameter, Object value) {
        this.parameter = parameter;
        this.value = value;
    }

    public static TileDataParameterUpdateMessage decode(PacketBuffer buf) {
        int id = buf.readInt();

        TileDataParameter parameter = TileDataManager.getParameter(id);
        Object value = null;

        if (parameter != null) {
            try {
                value = parameter.getSerializer().read(buf);
            } catch (Exception e) {
                // NO OP
            }
        }

        return new TileDataParameterUpdateMessage(parameter, value);
    }

    public static void encode(TileDataParameterUpdateMessage message, PacketBuffer buf) {
        buf.writeInt(message.parameter.getId());

        message.parameter.getSerializer().write(buf, message.value);
    }

    public static void handle(TileDataParameterUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Container c = ctx.get().getSender().containerMenu;

            if (c instanceof BaseContainer) {
                BiConsumer consumer = message.parameter.getValueConsumer();

                if (consumer != null) {
                    consumer.accept(((BaseContainer) c).getTile(), message.value);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
