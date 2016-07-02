package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageUtils;

public class MessageGridDelta implements IMessage, IMessageHandler<MessageGridDelta, IMessage> {
    private ItemStack stack;
    private int delta;
    private boolean craftable;

    public MessageGridDelta() {
    }

    public MessageGridDelta(ItemStack stack, int delta, boolean craftable) {
        this.stack = stack;
        this.delta = delta;
        this.craftable = craftable;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        stack = ByteBufUtils.readItemStack(buf);
        delta = buf.readInt();
        craftable = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(delta);
        buf.writeBoolean(craftable);
    }

    @Override
    public IMessage onMessage(MessageGridDelta message, MessageContext ctx) {
        for (ItemStack stack : RefinedStorage.INSTANCE.items) {
            if (RefinedStorageUtils.compareStackNoQuantity(stack, message.stack)) {
                if (stack.stackSize + message.delta == 0 && !message.craftable) {
                    RefinedStorage.INSTANCE.items.remove(stack);
                } else {
                    stack.stackSize += message.delta;
                }

                return null;
            }
        }

        RefinedStorage.INSTANCE.items.add(ItemHandlerHelper.copyStackWithSize(message.stack, message.delta));

        return null;
    }
}
