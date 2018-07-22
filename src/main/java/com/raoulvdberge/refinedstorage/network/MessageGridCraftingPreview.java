package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridCraftingPreview extends MessageHandlerPlayerToServer<MessageGridCraftingPreview> implements IMessage {
    private int hash;
    private int quantity;
    private boolean noPreview;
    private boolean fluids;

    public MessageGridCraftingPreview() {
    }

    public MessageGridCraftingPreview(int hash, int quantity, boolean noPreview, boolean fluids) {
        this.hash = hash;
        this.quantity = quantity;
        this.noPreview = noPreview;
        this.fluids = fluids;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hash = buf.readInt();
        quantity = buf.readInt();
        noPreview = buf.readBoolean();
        fluids = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hash);
        buf.writeInt(quantity);
        buf.writeBoolean(noPreview);
        buf.writeBoolean(fluids);
    }

    @Override
    public void handle(MessageGridCraftingPreview message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) container).getGrid();

            if (message.fluids) {
                if (grid.getFluidHandler() != null) {
                    grid.getFluidHandler().onCraftingPreviewRequested(player, message.hash, message.quantity, message.noPreview);
                }
            } else {
                if (grid.getItemHandler() != null) {
                    grid.getItemHandler().onCraftingPreviewRequested(player, message.hash, message.quantity, message.noPreview);
                }
            }
        }
    }
}
