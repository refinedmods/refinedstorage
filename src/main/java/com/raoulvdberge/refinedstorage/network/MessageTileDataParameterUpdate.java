package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.ContainerBase;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.function.BiConsumer;

public class MessageTileDataParameterUpdate extends MessageHandlerPlayerToServer<MessageTileDataParameterUpdate> implements IMessage {
    private TileDataParameter parameter;
    private Object value;

    public MessageTileDataParameterUpdate() {
    }

    public MessageTileDataParameterUpdate(TileDataParameter parameter, Object value) {
        this.parameter = parameter;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int id = buf.readInt();

        parameter = TileDataManager.getParameter(id);

        if (parameter != null) {
            try {
                value = parameter.getSerializer().read(new PacketBuffer(buf));
            } catch (Exception e) {
                // NO OP
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(parameter.getId());

        parameter.getSerializer().write((PacketBuffer) buf, value);
    }

    @Override
    public void handle(MessageTileDataParameterUpdate message, EntityPlayerMP player) {
        Container c = player.openContainer;

        if (c instanceof ContainerBase) {
            BiConsumer consumer = message.parameter.getValueConsumer();

            if (consumer != null) {
                consumer.accept(((ContainerBase) c).getTile(), message.value);
            }
        }
    }
}
