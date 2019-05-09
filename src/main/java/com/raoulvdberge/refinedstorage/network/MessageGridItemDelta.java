package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class MessageGridItemDelta implements IMessage, IMessageHandler<MessageGridItemDelta, IMessage> {
    @Nullable
    private INetwork network;
    private IStorageTracker<ItemStack> storageTracker;

    private List<Pair<ItemStack, Integer>> deltas;
    @Nullable
    private ItemStack stack;
    private int delta;

    @Nullable
    private GridStackItem gridStack;
    private List<Pair<GridStackItem, Integer>> gridStacks;

    public MessageGridItemDelta() {
    }

    public MessageGridItemDelta(@Nullable INetwork network, IStorageTracker<ItemStack> storageTracker, ItemStack stack, int delta) {
        this.network = network;
        this.storageTracker = storageTracker;
        this.stack = stack;
        this.delta = delta;
    }

    public MessageGridItemDelta(@Nullable INetwork network, IStorageTracker<ItemStack> storageTracker, List<Pair<ItemStack, Integer>> deltas) {
        this.network = network;
        this.storageTracker = storageTracker;
        this.deltas = deltas;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        if (size == 1) {
            gridStack = new GridStackItem(buf);
            delta = buf.readInt();
        } else {
            gridStacks = new LinkedList<>();

            for (int i = 0; i < size; ++i) {
                gridStacks.add(Pair.of(new GridStackItem(buf), buf.readInt()));
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (stack != null) {
            buf.writeInt(1);

            StackUtils.writeItemStack(buf, stack, network, delta == 0);

            IStorageTracker.IStorageTrackerEntry entry = storageTracker.get(stack);
            buf.writeBoolean(entry != null);
            if (entry != null) {
                buf.writeLong(entry.getTime());
                ByteBufUtils.writeUTF8String(buf, entry.getName());
            }

            buf.writeInt(delta);
        } else {
            buf.writeInt(deltas.size());

            for (Pair<ItemStack, Integer> delta : deltas) {
                StackUtils.writeItemStack(buf, delta.getLeft(), network, false);

                IStorageTracker.IStorageTrackerEntry entry = storageTracker.get(delta.getLeft());
                buf.writeBoolean(entry != null);
                if (entry != null) {
                    buf.writeLong(entry.getTime());
                    ByteBufUtils.writeUTF8String(buf, entry.getName());
                }

                buf.writeInt(delta.getRight());
            }
        }
    }

    @Override
    public IMessage onMessage(MessageGridItemDelta message, MessageContext ctx) {
        GuiBase.executeLater(GuiGrid.class, grid -> {
            if (message.gridStack != null) {
                grid.getView().postChange(message.gridStack, message.delta);
            } else {
                message.gridStacks.forEach(p -> grid.getView().postChange(p.getLeft(), p.getRight()));
            }

            grid.getView().sort();
        });

        return null;
    }
}
