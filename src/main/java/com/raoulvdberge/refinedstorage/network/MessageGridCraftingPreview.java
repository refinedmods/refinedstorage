package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridCraftingPreview extends MessageHandlerPlayerToServer<MessageGridCraftingPreview> implements IMessage {
    private int hash;
    private int quantity;
    private boolean noPreview;

    public MessageGridCraftingPreview() {
    }

    public MessageGridCraftingPreview(int hash, int quantity, boolean noPreview) {
        this.hash = hash;
        this.quantity = quantity;
        this.noPreview = noPreview;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hash = buf.readInt();
        quantity = buf.readInt();
        noPreview = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hash);
        buf.writeInt(quantity);
        buf.writeBoolean(noPreview);
    }

    @Override
    public void handle(MessageGridCraftingPreview message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) container).getGrid();

            if (grid.getNetwork() != null) {
                grid.getNetwork().getItemGridHandler().onCraftingPreviewRequested(player, message.hash, message.quantity, message.noPreview);
            }
        }
    }
}
