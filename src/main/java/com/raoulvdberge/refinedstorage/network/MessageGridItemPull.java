package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridItemPull extends MessageHandlerPlayerToServer<MessageGridItemPull> implements IMessage {
    private int hash;
    private int flags;

    public MessageGridItemPull() {
    }

    public MessageGridItemPull(int hash, int flags) {
        this.hash = hash;
        this.flags = flags;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hash = buf.readInt();
        flags = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hash);
        buf.writeInt(flags);
    }

    @Override
    public void handle(MessageGridItemPull message, ServerPlayerEntity player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) container).getGrid();

            if (grid.getItemHandler() != null) {
                grid.getItemHandler().onExtract(player, message.hash, message.flags);
            }
        }
    }
}
