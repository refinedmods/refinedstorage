package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

    private List<Pair<ItemStack, Integer>> deltas;
    @Nullable
    private ItemStack stack;
    private int delta;

    @Nullable
    private GridStackItem gridStack;
    private List<Pair<GridStackItem, Integer>> gridStacks;

    public MessageGridItemDelta() {
    }

    public MessageGridItemDelta(@Nullable INetwork network, ItemStack stack, int delta) {
        this.network = network;
        this.stack = stack;
        this.delta = delta;
    }

    public MessageGridItemDelta(@Nullable INetwork network, List<Pair<ItemStack, Integer>> deltas) {
        this.network = network;
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

            StackUtils.writeItemStack(buf, stack, network, false);
            buf.writeInt(delta);
        } else {
            buf.writeInt(deltas.size());

            for (Pair<ItemStack, Integer> delta : deltas) {
                StackUtils.writeItemStack(buf, delta.getLeft(), network, false);
                buf.writeInt(delta.getRight());
            }
        }
    }

    @Override
    public IMessage onMessage(MessageGridItemDelta message, MessageContext ctx) {
        if (message.gridStack != null) {
            process(message.gridStack, message.delta);
        } else {
            message.gridStacks.forEach(p -> process(p.getLeft(), p.getRight()));
        }

        GuiGrid.markForSorting();

        return null;
    }

    private void process(GridStackItem gridStack, int delta) {
        Item item = gridStack.getStack().getItem();

        for (GridStackItem stack : GuiGrid.ITEMS.get(item)) {
            if (stack.equals(gridStack)) {
                if (stack.getStack().getCount() + delta == 0) {
                    if (gridStack.isCraftable()) {
                        stack.setDisplayCraftText(true);
                    } else {
                        GuiGrid.ITEMS.remove(item, stack);
                    }
                } else {
                    if (stack.doesDisplayCraftText()) {
                        stack.setDisplayCraftText(false);

                        stack.getStack().setCount(delta);
                    } else {
                        stack.getStack().grow(delta);
                    }
                }

                return;
            }
        }

        gridStack.getStack().setCount(delta);

        GuiGrid.ITEMS.put(item, gridStack);
    }
}
