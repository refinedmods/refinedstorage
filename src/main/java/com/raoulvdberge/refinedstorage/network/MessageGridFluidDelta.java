package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackFluid;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageGridFluidDelta implements IMessage, IMessageHandler<MessageGridFluidDelta, IMessage> {
    private FluidStack stack;
    private int delta;

    private GridStackFluid clientStack;

    public MessageGridFluidDelta() {
    }

    public MessageGridFluidDelta(FluidStack stack, int delta) {
        this.stack = stack;
        this.delta = delta;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        clientStack = new GridStackFluid(RSUtils.readFluidStack(buf));
        delta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        RSUtils.writeFluidStack(buf, stack);
        buf.writeInt(delta);
    }

    @Override
    public IMessage onMessage(MessageGridFluidDelta message, MessageContext ctx) {
        Fluid fluid = message.clientStack.getStack().getFluid();

        for (GridStackFluid stack : GuiGrid.FLUIDS.get(fluid)) {
            if (stack.equals(message.clientStack)) {
                if (stack.getStack().amount + message.delta == 0) {
                    GuiGrid.FLUIDS.remove(fluid, stack);
                } else {
                    stack.getStack().amount += message.delta;
                }

                GuiGrid.markForSorting();

                return null;
            }
        }

        GuiGrid.FLUIDS.put(fluid, message.clientStack);
        GuiGrid.markForSorting();

        return null;
    }
}
