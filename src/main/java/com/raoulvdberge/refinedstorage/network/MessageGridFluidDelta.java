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
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class MessageGridFluidDelta implements IMessage, IMessageHandler<MessageGridFluidDelta, IMessage> {
    @Nullable
    private INetwork network;
    private IStorageTracker<FluidStack> storageTracker;
    private FluidStack stack;
    private int delta;

    private GridStackFluid gridStack;

    public MessageGridFluidDelta() {
    }

    public MessageGridFluidDelta(@Nullable INetwork network, IStorageTracker<FluidStack> storageTracker, FluidStack stack, int delta) {
        this.network = network;
        this.storageTracker = storageTracker;
        this.stack = stack;
        this.delta = delta;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        Pair<Integer, FluidStack> hashAndFluidStack = StackUtils.readFluidStackAndHash(buf);

        gridStack = new GridStackFluid(hashAndFluidStack.getLeft(), hashAndFluidStack.getRight(), buf.readBoolean() ? new StorageTrackerEntry(buf) : null, buf.readBoolean(), false);
        delta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        StackUtils.writeFluidStackAndHash(buf, stack);

        IStorageTracker.IStorageTrackerEntry entry = storageTracker.get(stack);
        buf.writeBoolean(entry != null);
        if (entry != null) {
            buf.writeLong(entry.getTime());
            ByteBufUtils.writeUTF8String(buf, entry.getName());
        }

        if (network != null) {
            buf.writeBoolean(network.getCraftingManager().getPattern(stack) != null);
        } else {
            buf.writeBoolean(false);
        }

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
