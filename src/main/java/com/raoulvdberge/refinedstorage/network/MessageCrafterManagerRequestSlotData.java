package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.ContainerCrafterManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
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
    protected void handle(MessageCrafterManagerRequestSlotData message, ServerPlayerEntity player) {
        if (player.openContainer instanceof ContainerCrafterManager) {
            for (IContainerListener listener : ((ContainerCrafterManager) player.openContainer).getListeners()) {
                if (listener instanceof ContainerCrafterManager.CrafterManagerListener) {
                    ContainerCrafterManager.CrafterManagerListener cmListener = (ContainerCrafterManager.CrafterManagerListener) listener;

                    if (cmListener.getPlayer() == player) {
                        cmListener.setReceivedContainerData();
                        cmListener.sendAllContents(player.openContainer, player.openContainer.getInventory());
                    }
                }
            }
        }
    }
}
