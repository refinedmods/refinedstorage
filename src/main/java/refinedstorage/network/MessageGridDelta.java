package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.container.ContainerGrid;

import java.util.List;

public class MessageGridDelta implements IMessage, IMessageHandler<MessageGridDelta, IMessage> {
    private ItemStack stack;
    private int delta;

    public MessageGridDelta() {
    }

    public MessageGridDelta(ItemStack stack, int delta) {
        this.stack = stack;
        this.delta = delta;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.stack = ByteBufUtils.readItemStack(buf);
        this.delta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(delta);
    }

    @Override
    public IMessage onMessage(MessageGridDelta message, MessageContext ctx) {
        Container container = Minecraft.getMinecraft().thePlayer.openContainer;

        if (container instanceof ContainerGrid) {
            List<ItemStack> items = ((ContainerGrid) container).getGrid().getItems();

            for (ItemStack item : items) {
                if (RefinedStorageUtils.compareStackNoQuantity(item, message.stack)) {
                    item.stackSize += message.delta;

                    if (item.stackSize <= 0) {
                        items.remove(item);
                    }

                    return null;
                }
            }

            if (message.delta > 0) {
                items.add(ItemHandlerHelper.copyStackWithSize(message.stack, message.delta));
            }
        }

        return null;
    }
}
