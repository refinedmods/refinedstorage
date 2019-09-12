package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskReadException;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.GuiCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class MessageCraftingMonitorElements implements IMessage, IMessageHandler<MessageCraftingMonitorElements, IMessage> {
    private ICraftingMonitor craftingMonitor;

    private List<IGridTab> tasks = new ArrayList<>();

    public MessageCraftingMonitorElements() {
    }

    public MessageCraftingMonitorElements(ICraftingMonitor craftingMonitor) {
        this.craftingMonitor = craftingMonitor;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            UUID id = UUID.fromString(ByteBufUtils.readUTF8String(buf));

            ICraftingRequestInfo requested = null;
            try {
                requested = API.instance().createCraftingRequestInfo(ByteBufUtils.readTag(buf));
            } catch (CraftingTaskReadException e) {
                e.printStackTrace();
            }

            int qty = buf.readInt();
            long executionStarted = buf.readLong();
            int percentage = buf.readInt();

            List<ICraftingMonitorElement> elements = new ArrayList<>();

            int elementCount = buf.readInt();

            for (int j = 0; j < elementCount; ++j) {
                Function<ByteBuf, ICraftingMonitorElement> factory = API.instance().getCraftingMonitorElementRegistry().get(ByteBufUtils.readUTF8String(buf));

                if (factory != null) {
                    elements.add(factory.apply(buf));
                }
            }

            tasks.add(new GuiCraftingMonitor.CraftingMonitorTask(id, requested, qty, executionStarted, percentage, elements));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(craftingMonitor.getTasks().size());

        for (ICraftingTask task : craftingMonitor.getTasks()) {
            ByteBufUtils.writeUTF8String(buf, task.getId().toString());
            ByteBufUtils.writeTag(buf, task.getRequested().writeToNbt());
            buf.writeInt(task.getQuantity());
            buf.writeLong(task.getExecutionStarted());
            buf.writeInt(task.getCompletionPercentage());

            List<ICraftingMonitorElement> elements = task.getCraftingMonitorElements();

            buf.writeInt(elements.size());

            for (ICraftingMonitorElement element : elements) {
                ByteBufUtils.writeUTF8String(buf, element.getId());

                element.write(buf);
            }
        }
    }

    @Override
    public IMessage onMessage(MessageCraftingMonitorElements message, MessageContext ctx) {
        GuiBase.executeLater(GuiCraftingMonitor.class, craftingMonitor -> craftingMonitor.setTasks(message.tasks));

        return null;
    }
}
