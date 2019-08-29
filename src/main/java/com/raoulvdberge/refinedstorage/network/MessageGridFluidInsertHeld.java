package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
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
    public void handle(MessageGridFluidInsertHeld message, ServerPlayerEntity player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) container).getGrid();

            if (grid.getFluidHandler() != null) {
                grid.getFluidHandler().onInsertHeldContainer(player);
            }
        }
    }
}
