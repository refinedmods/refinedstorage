package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridFluidInsertHeld extends MessageHandlerPlayerToServer<MessageGridFluidInsertHeld> implements IMessage {
    public MessageGridFluidInsertHeld() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    public void handle(MessageGridFluidInsertHeld message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IFluidGridHandler handler = ((ContainerGrid) container).getGrid().getFluidHandler();

            if (handler != null) {
                handler.onInsertHeldContainer(player);
            }
        }
    }
}
