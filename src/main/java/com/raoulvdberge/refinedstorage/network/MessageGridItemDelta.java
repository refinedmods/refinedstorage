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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MessageGridItemDelta implements IMessage, IMessageHandler<MessageGridItemDelta, IMessage> {
    @Nullable
    private INetwork network;
    private IStorageTracker<ItemStack> storageTracker;

    private List<Pair<ItemStack, Integer>> deltas;
    private List<Pair<GridStackItem, Integer>> gridStacks;

    @SuppressWarnings("unused")
    public MessageGridItemDelta() {
    }

    public MessageGridItemDelta(@Nullable INetwork network, IStorageTracker<ItemStack> storageTracker, ItemStack stack, int delta) {
        this(network, storageTracker, Arrays.asList(Pair.of(stack, delta)));
    }

    public MessageGridItemDelta(@Nullable INetwork network, IStorageTracker<ItemStack> storageTracker, List<Pair<ItemStack, Integer>> deltas) {
        this.network = network;
        this.storageTracker = storageTracker;
        this.deltas = deltas;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        List<Pair<GridStackItem, Integer>> gridStacks = new LinkedList<>();

        for (int i = 0; i < size; ++i) {
            GridStackItem stack = new GridStackItem(buf);
            int delta = buf.readInt();
            gridStacks.add(Pair.of(stack, delta));
        }

        this.gridStacks = gridStacks;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(deltas.size());

        for (Pair<ItemStack, Integer> deltaPair : deltas) {
            writeSingleItem(buf, deltaPair.getLeft(), deltaPair.getRight());
        }
    }

    private void writeSingleItem(ByteBuf buf, ItemStack stack, Integer delta) {
        StackUtils.writeItemStack(buf, stack, network, false);

        IStorageTracker.IStorageTrackerEntry entry = storageTracker.get(stack);
        buf.writeBoolean(entry != null);
        if (entry != null) {
            buf.writeLong(entry.getTime());
            ByteBufUtils.writeUTF8String(buf, entry.getName());
        }

        buf.writeInt(delta);
    }

    @Override
    public IMessage onMessage(MessageGridItemDelta message, MessageContext ctx) {
        GuiBase.executeLater(GuiGrid.class, grid -> {
            message.gridStacks.forEach(p -> grid.getView().postChange(p.getLeft(), p.getRight()));

            grid.getView().sort();
        });

        return null;
    }
}
