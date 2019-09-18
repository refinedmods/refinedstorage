package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.BaseContainer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MessageTileDataParameterUpdate {
    private TileDataParameter parameter;
    private Object value;

    public MessageTileDataParameterUpdate(TileDataParameter parameter, Object value) {
        this.parameter = parameter;
        this.value = value;
    }

    public static MessageTileDataParameterUpdate decode(PacketBuffer buf) {
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

        return new MessageTileDataParameterUpdate(parameter, value);
    }

    public static void encode(MessageTileDataParameterUpdate message, PacketBuffer buf) {
        buf.writeInt(message.parameter.getId());

        message.parameter.getSerializer().write(buf, message.value);
    }

    public static void handle(MessageTileDataParameterUpdate message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Container c = ctx.get().getSender().openContainer;

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
