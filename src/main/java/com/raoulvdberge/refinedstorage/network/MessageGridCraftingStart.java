package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridCraftingStart extends MessageHandlerPlayerToServer<MessageGridCraftingStart> implements IMessage {
    private int hash;
    private int quantity;
    private boolean fluids;

    public MessageGridCraftingStart() {
    }

    public MessageGridCraftingStart(int hash, int quantity, boolean fluids) {
        this.hash = hash;
        this.quantity = quantity;
        this.fluids = fluids;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hash = buf.readInt();
        quantity = buf.readInt();
        fluids = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hash);
        buf.writeInt(quantity);
        buf.writeBoolean(fluids);
    }

    @Override
    public void handle(MessageGridCraftingStart message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) container).getGrid();

            if (message.fluids) {
                if (grid.getFluidHandler() != null) {
                    grid.getFluidHandler().onCraftingRequested(player, message.hash, message.quantity);
                }
            } else {
                if (grid.getItemHandler() != null) {
                    grid.getItemHandler().onCraftingRequested(player, message.hash, message.quantity);
                }
            }
        }
    }
}
