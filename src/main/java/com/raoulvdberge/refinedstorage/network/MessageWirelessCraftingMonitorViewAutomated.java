package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import com.raoulvdberge.refinedstorage.item.ItemWirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.ICraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageWirelessCraftingMonitorViewAutomated extends MessageHandlerPlayerToServer<MessageWirelessCraftingMonitorViewAutomated> implements IMessage {
    private boolean viewAutomated;

    public MessageWirelessCraftingMonitorViewAutomated() {
    }

    public MessageWirelessCraftingMonitorViewAutomated(boolean viewAutomated) {
        this.viewAutomated = viewAutomated;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        viewAutomated = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(viewAutomated);
    }

    @Override
    public void handle(MessageWirelessCraftingMonitorViewAutomated message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerCraftingMonitor) {
            ICraftingMonitor craftingMonitor = ((ContainerCraftingMonitor) player.openContainer).getCraftingMonitor();

            if (craftingMonitor instanceof WirelessCraftingMonitor) {
                ItemStack stack = ((WirelessCraftingMonitor) craftingMonitor).getStack();

                ItemWirelessCraftingMonitor.setViewAutomated(stack, message.viewAutomated);
            }
        }
    }
}
