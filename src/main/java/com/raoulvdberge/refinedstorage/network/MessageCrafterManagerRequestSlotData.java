package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.ContainerCrafterManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageCrafterManagerRequestSlotData extends MessageHandlerPlayerToServer<MessageCrafterManagerRequestSlotData> implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    @Override
    protected void handle(MessageCrafterManagerRequestSlotData message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerCrafterManager) {
            for (IContainerListener listener : ((ContainerCrafterManager) player.openContainer).getListeners()) {
                ContainerCrafterManager.Listener crafterListener = (ContainerCrafterManager.Listener) listener;

                if (crafterListener.getPlayer() == player) {
                    crafterListener.setReceivedContainerData();
                    crafterListener.sendAllContents(player.openContainer, player.openContainer.getInventory());
                }
            }
        }
    }
}
