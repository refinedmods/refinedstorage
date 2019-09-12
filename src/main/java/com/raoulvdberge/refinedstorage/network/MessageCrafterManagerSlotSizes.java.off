package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.ContainerCrafterManager;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.GuiCrafterManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessageCrafterManagerSlotSizes implements IMessage, IMessageHandler<MessageCrafterManagerSlotSizes, IMessage> {
    private Map<String, List<IItemHandlerModifiable>> containerData;
    private Map<String, Integer> containerDataClient = new LinkedHashMap<>();

    public MessageCrafterManagerSlotSizes(Map<String, List<IItemHandlerModifiable>> containerData) {
        this.containerData = containerData;
    }

    public MessageCrafterManagerSlotSizes() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            containerDataClient.put(ByteBufUtils.readUTF8String(buf), buf.readInt());
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(containerData.size());

        for (Map.Entry<String, List<IItemHandlerModifiable>> entry : containerData.entrySet()) {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());

            int slots = 0;
            for (IItemHandlerModifiable handler : entry.getValue()) {
                slots += handler.getSlots();
            }
            buf.writeInt(slots);
        }
    }

    @Override
    public IMessage onMessage(MessageCrafterManagerSlotSizes message, MessageContext ctx) {
        GuiBase.executeLater(GuiCrafterManager.class, crafterManager -> {
            ((ContainerCrafterManager) crafterManager.inventorySlots).initSlots(message.containerDataClient);

            RS.INSTANCE.network.sendToServer(new MessageCrafterManagerRequestSlotData());
        });

        return null;
    }
}
