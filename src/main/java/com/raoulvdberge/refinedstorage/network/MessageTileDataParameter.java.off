package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTileDataParameter implements IMessage, IMessageHandler<MessageTileDataParameter, IMessage> {
    private TileEntity tile;
    private TileDataParameter parameter;
    private boolean initial;

    public MessageTileDataParameter() {
    }

    public MessageTileDataParameter(TileEntity tile, TileDataParameter parameter, boolean initial) {
        this.tile = tile;
        this.parameter = parameter;
        this.initial = initial;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int id = buf.readInt();
        boolean initial = buf.readBoolean();

        TileDataParameter parameter = TileDataManager.getParameter(id);

        if (parameter != null) {
            try {
                parameter.setValue(initial, parameter.getSerializer().read(new PacketBuffer(buf)));
            } catch (Exception e) {
                // NO OP
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(parameter.getId());
        buf.writeBoolean(initial);

        parameter.getSerializer().write((PacketBuffer) buf, parameter.getValueProducer().apply(tile));
    }

    @Override
    public IMessage onMessage(MessageTileDataParameter message, MessageContext ctx) {
        return null;
    }
}
