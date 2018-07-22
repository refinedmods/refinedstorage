package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageTrackerEntry;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackFluid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageGridFluidDelta implements IMessage, IMessageHandler<MessageGridFluidDelta, IMessage> {
    private INetwork network;
    private FluidStack stack;
    private int delta;

    private GridStackFluid gridStack;

    public MessageGridFluidDelta() {
    }

    public MessageGridFluidDelta(INetwork network, FluidStack stack, int delta) {
        this.network = network;
        this.stack = stack;
        this.delta = delta;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gridStack = new GridStackFluid(buf.readInt(), StackUtils.readFluidStack(buf), buf.readBoolean() ? new StorageTrackerEntry(buf) : null, buf.readBoolean(), false);
        delta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        StackUtils.writeFluidStack(buf, stack);

        IStorageTracker.IStorageTrackerEntry entry = network.getFluidStorageTracker().get(stack);
        buf.writeBoolean(entry != null);
        if (entry != null) {
            buf.writeLong(entry.getTime());
            ByteBufUtils.writeUTF8String(buf, entry.getName());
        }

        buf.writeBoolean(network.getCraftingManager().getPattern(stack) != null);

        buf.writeInt(delta);
    }

    @Override
    public IMessage onMessage(MessageGridFluidDelta message, MessageContext ctx) {
        GuiBase.executeLater(GuiGrid.class, grid -> {
            grid.getView().postChange(message.gridStack, message.delta);
            grid.getView().sort();
        });

        return null;
    }
}
