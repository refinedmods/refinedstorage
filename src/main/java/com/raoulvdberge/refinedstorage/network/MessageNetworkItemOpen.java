package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.item.ItemNetworkItem;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageNetworkItemOpen extends MessageHandlerPlayerToServer<MessageNetworkItemOpen> implements IMessage {
    private int slotId;

    public MessageNetworkItemOpen() {
    }

    public MessageNetworkItemOpen(int slotId) {
        this.slotId = slotId;
    }

    @Override
    protected void handle(MessageNetworkItemOpen message, EntityPlayerMP player) {
        ItemStack stack = player.inventory.getStackInSlot(message.slotId);

        if (stack.getItem() instanceof ItemNetworkItem) {
            ((ItemNetworkItem) stack.getItem()).applyNetwork(stack, n -> n.getNetworkItemHandler().open(player, stack, message.slotId), player::sendMessage);
        } else if (stack.getItem() == Item.getItemFromBlock(RSBlocks.PORTABLE_GRID)) { // @Hack
            API.instance().getGridManager().openGrid(PortableGrid.ID, player, stack, message.slotId);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slotId);
    }
}
