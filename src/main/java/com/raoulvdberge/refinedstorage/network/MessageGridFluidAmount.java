package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridFluidAmount extends MessageHandlerPlayerToServer<MessageGridFluidAmount> implements IMessage {
    private int slot;
    private int amount;

    public MessageGridFluidAmount(int slot, int amount) {
        this.slot = slot;
        this.amount = amount;
    }

    public MessageGridFluidAmount() {
        // NO OP
    }

    @Override
    protected void handle(MessageGridFluidAmount message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid && message.slot >= 0 && message.amount > 0 && message.amount <= Fluid.BUCKET_VOLUME) {
            IGrid grid = ((ContainerGrid) container).getGrid();

            if (grid instanceof NetworkNodeGrid) {
                NetworkNodeGrid node = (NetworkNodeGrid) grid;

                if (message.slot < node.getMatrixProcessingFluids().getSlots()) {
                    node.getMatrixProcessingFluids().getStackInSlot(message.slot).setCount(message.amount);
                }
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
        amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        buf.writeInt(amount);
    }
}
