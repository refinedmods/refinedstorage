package com.raoulvdberge.refinedstorage.network;

import com.google.common.base.Optional;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import com.raoulvdberge.refinedstorage.item.ItemWirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageWirelessCraftingMonitorSettings extends MessageHandlerPlayerToServer<MessageWirelessCraftingMonitorSettings> implements IMessage {
    private int size;
    private Optional<UUID> tabSelected = Optional.absent();
    private int tabPage;

    public MessageWirelessCraftingMonitorSettings() {
    }

    public MessageWirelessCraftingMonitorSettings(int size, Optional<UUID> tabSelected, int tabPage) {
        this.size = size;
        this.tabSelected = tabSelected;
        this.tabPage = tabPage;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        size = buf.readInt();

        if (buf.readBoolean()) {
            tabSelected = Optional.of(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        }

        tabPage = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(size);

        buf.writeBoolean(tabSelected.isPresent());
        if (tabSelected.isPresent()) {
            ByteBufUtils.writeUTF8String(buf, tabSelected.get().toString());
        }

        buf.writeInt(tabPage);
    }

    @Override
    public void handle(MessageWirelessCraftingMonitorSettings message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerCraftingMonitor && IGrid.isValidSize(message.size)) {
            ItemStack stack = ((WirelessCraftingMonitor) ((ContainerCraftingMonitor) player.openContainer).getCraftingMonitor()).getStack();

            ItemWirelessCraftingMonitor.setSize(stack, message.size);
            ItemWirelessCraftingMonitor.setTabPage(stack, message.tabPage);
            ItemWirelessCraftingMonitor.setTabSelected(stack, message.tabSelected);
        }
    }
}
