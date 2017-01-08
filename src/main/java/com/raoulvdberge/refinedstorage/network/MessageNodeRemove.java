package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeManager;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageNodeRemove implements IMessage, IMessageHandler<MessageNodeRemove, IMessage> {
    private int dim;
    private BlockPos pos;

    public MessageNodeRemove() {
    }

    public MessageNodeRemove(int dim, BlockPos pos) {
        this.dim = dim;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        dim = buf.readInt();
        pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dim);
        buf.writeLong(pos.toLong());
    }

    @Override
    public IMessage onMessage(MessageNodeRemove message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            INetworkNodeManager manager = API.instance().getNetworkNodeManager(message.dim);

            manager.removeNode(message.pos);
        });

        return null;
    }
}