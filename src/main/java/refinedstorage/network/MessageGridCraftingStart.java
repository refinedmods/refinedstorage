package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.container.ContainerGrid;

public class MessageGridCraftingStart extends MessageHandlerPlayerToServer<MessageGridCraftingStart> implements IMessage {
    private ItemStack stack;
    private int quantity;

    public MessageGridCraftingStart() {
    }

    public MessageGridCraftingStart(ItemStack stack, int quantity) {
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
    public void handle(MessageGridCraftingStart message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IGridHandler handler = ((ContainerGrid) container).getGrid().getGridHandler();

            if (handler != null) {
                handler.onCraftingRequested(message.stack, message.quantity);
            }
        }
    }
}
