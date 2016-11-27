package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridCraftingPreview extends MessageHandlerPlayerToServer<MessageGridCraftingPreview> implements IMessage {
    private ItemStack stack;
    private int quantity;

    public MessageGridCraftingPreview() {
    }

    public MessageGridCraftingPreview(ItemStack stack, int quantity) {
        this.stack = stack;
        this.quantity = quantity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        stack = ByteBufUtils.readItemStack(buf);
        quantity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(quantity);
    }

    @Override
    public void handle(MessageGridCraftingPreview message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            ((ContainerGrid) container).getGrid().getItemHandler().onCraftingPreviewRequested(player, message.stack, message.quantity);
        }
    }
}
