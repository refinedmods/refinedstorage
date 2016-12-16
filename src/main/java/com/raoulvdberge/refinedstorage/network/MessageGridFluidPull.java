package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridFluidPull extends MessageHandlerPlayerToServer<MessageGridFluidPull> implements IMessage {
    private int hash;
    private boolean shift;

    public MessageGridFluidPull() {
    }

    public MessageGridFluidPull(int hash, boolean shift) {
        this.hash = hash;
        this.shift = shift;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hash = buf.readInt();
        shift = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hash);
        buf.writeBoolean(shift);
    }

    @Override
    public void handle(MessageGridFluidPull message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IFluidGridHandler handler = ((ContainerGrid) container).getGrid().getFluidHandler();

            if (handler != null) {
                handler.onExtract(player, message.hash, message.shift);
            }
        }
    }
}
