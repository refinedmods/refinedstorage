package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridItemInsertHeld extends MessageHandlerPlayerToServer<MessageGridItemInsertHeld> implements IMessage {
    private boolean single;

    public MessageGridItemInsertHeld() {
    }

    public MessageGridItemInsertHeld(boolean single) {
        this.single = single;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        single = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(single);
    }

    @Override
    public void handle(MessageGridItemInsertHeld message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) container).getGrid();

            if (grid.getItemHandler() != null) {
                grid.getItemHandler().onInsertHeldItem(player, message.single);
            }
        }
    }
}
