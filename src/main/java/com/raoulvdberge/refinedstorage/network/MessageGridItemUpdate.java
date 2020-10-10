package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.gui.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.gui.grid.view.GridViewImpl;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MessageGridItemUpdate implements IMessage, IMessageHandler<MessageGridItemUpdate, IMessage> {
    private Consumer<ByteBuf> sendHandler;
    private boolean canCraft;
    private List<IGridStack> stacks = new ArrayList<>();

    public MessageGridItemUpdate() {
    }

    public MessageGridItemUpdate(INetwork network, boolean canCraft) {
        this.sendHandler = (buf) -> {
            int size = network.getItemStorageCache().getList().getStacks().size();

            for (ICraftingPattern pattern : network.getCraftingManager().getPatterns()) {
                size += pattern.getOutputs().size();
            }

            buf.writeInt(size);

            for (ItemStack stack : network.getItemStorageCache().getList().getStacks()) {
                StackUtils.writeItemStack(buf, stack, network, false);

                IStorageTracker.IStorageTrackerEntry entry = network.getItemStorageTracker().get(stack);
                buf.writeBoolean(entry != null);
                if (entry != null) {
                    buf.writeLong(entry.getTime());
                    ByteBufUtils.writeUTF8String(buf, entry.getName());
                }
            }

            for (ICraftingPattern pattern : network.getCraftingManager().getPatterns()) {
                for (ItemStack output : pattern.getOutputs()) {
                    StackUtils.writeItemStack(buf, output, network, true);

                    IStorageTracker.IStorageTrackerEntry entry = network.getItemStorageTracker().get(output);
                    buf.writeBoolean(entry != null);
                    if (entry != null) {
                        buf.writeLong(entry.getTime());
                        ByteBufUtils.writeUTF8String(buf, entry.getName());
                    }
                }
            }
        };

        this.canCraft = canCraft;
    }

    public MessageGridItemUpdate(Consumer<ByteBuf> sendHandler, boolean canCraft) {
        this.sendHandler = sendHandler;
        this.canCraft = canCraft;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        canCraft = buf.readBoolean();

        int items = buf.readInt();

        for (int i = 0; i < items; ++i) {
            this.stacks.add(new GridStackItem(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(canCraft);

        sendHandler.accept(buf);
    }

    @Override
    public IMessage onMessage(MessageGridItemUpdate message, MessageContext ctx) {
        GuiBase.executeLater(GuiGrid.class, grid -> {
            grid.setView(new GridViewImpl(grid, GuiGrid.getDefaultSorter(), GuiGrid.getSorters()));
            grid.getView().setCanCraft(message.canCraft);
            grid.getView().setStacks(message.stacks);
            grid.getView().sort();
        });

        return null;
    }
}
