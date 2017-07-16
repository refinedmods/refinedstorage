package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackFluid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class MessageGridFluidUpdate implements IMessage, IMessageHandler<MessageGridFluidUpdate, IMessage> {
    private INetwork network;
    private boolean canCraft;
    private List<GridStackFluid> stacks = new ArrayList<>();

    public MessageGridFluidUpdate() {
    }

    public MessageGridFluidUpdate(INetwork network, boolean canCraft) {
        this.network = network;
        this.canCraft = canCraft;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        canCraft = buf.readBoolean();

        int items = buf.readInt();

        for (int i = 0; i < items; ++i) {
            this.stacks.add(new GridStackFluid(StackUtils.readFluidStack(buf)));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(canCraft);

        buf.writeInt(network.getFluidStorageCache().getList().getStacks().size());

        for (FluidStack stack : network.getFluidStorageCache().getList().getStacks()) {
            StackUtils.writeFluidStack(buf, stack);
        }
    }

    @Override
    public IMessage onMessage(MessageGridFluidUpdate message, MessageContext ctx) {
        GuiGrid.CAN_CRAFT = message.canCraft;

        GuiGrid.FLUIDS.clear();

        for (GridStackFluid item : message.stacks) {
            GuiGrid.FLUIDS.put(item.getStack().getFluid(), item);
        }

        GuiGrid.markForSorting();

        return null;
    }
}
