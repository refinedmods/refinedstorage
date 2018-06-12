package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import com.raoulvdberge.refinedstorage.item.ItemWirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageWirelessCraftingMonitorSize extends MessageHandlerPlayerToServer<MessageWirelessCraftingMonitorSize> implements IMessage {
    private int size;

    public MessageWirelessCraftingMonitorSize() {
    }

    public MessageWirelessCraftingMonitorSize(int size) {
        this.size = size;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        size = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(size);
    }

    @Override
    public void handle(MessageWirelessCraftingMonitorSize message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerCraftingMonitor && IGrid.isValidSize(message.size)) {
            ItemWirelessCraftingMonitor.setSize(((WirelessCraftingMonitor) ((ContainerCraftingMonitor) player.openContainer).getCraftingMonitor()).getStack(), message.size);
        }
    }
}
