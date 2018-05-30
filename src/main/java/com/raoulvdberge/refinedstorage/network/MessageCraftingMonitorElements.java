package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.GuiCraftingMonitor;
import com.raoulvdberge.refinedstorage.gui.grid.filtering.GridFilterFilter;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MessageCraftingMonitorElements implements IMessage, IMessageHandler<MessageCraftingMonitorElements, IMessage> {
    private ICraftingMonitor craftingMonitor;

    private List<ICraftingMonitorElement> elements = new ArrayList<>();

    public MessageCraftingMonitorElements() {
    }

    public MessageCraftingMonitorElements(ICraftingMonitor craftingMonitor) {
        this.craftingMonitor = craftingMonitor;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            Function<ByteBuf, ICraftingMonitorElement> factory = API.instance().getCraftingMonitorElementRegistry().get(ByteBufUtils.readUTF8String(buf));

            if (factory != null) {
                elements.add(factory.apply(buf));
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        List<ICraftingMonitorElement> elements = new ArrayList<>();

        for (ICraftingTask task : craftingMonitor.getTasks()) {
            ItemStack stack = task.getRequested();

            if (stack == null || GridFilterFilter.accepts(craftingMonitor.getFilters(), stack, Item.REGISTRY.getNameForObject(stack.getItem()).getResourceDomain())) {
                elements.addAll(task.getCraftingMonitorElements());
            }
        }

        buf.writeInt(elements.size());

        for (ICraftingMonitorElement task : elements) {
            ByteBufUtils.writeUTF8String(buf, task.getId());

            task.write(buf);
        }
    }

    @Override
    public IMessage onMessage(MessageCraftingMonitorElements message, MessageContext ctx) {
        GuiBase.executeLater(GuiCraftingMonitor.class, craftingMonitor -> craftingMonitor.setElements(message.elements));

        return null;
    }
}
