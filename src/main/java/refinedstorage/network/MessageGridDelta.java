package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.apiimpl.storage.ClientStack;

public class MessageGridDelta implements IMessage, IMessageHandler<MessageGridDelta, IMessage> {
    private INetworkMaster network;
    private ItemStack stack;
    private int delta;

    private ClientStack clientStack;

    public MessageGridDelta() {
    }

    public MessageGridDelta(INetworkMaster network, ItemStack stack, int delta) {
        this.network = network;
        this.stack = stack;
        this.delta = delta;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        clientStack = RefinedStorageUtils.readClientStack(buf);
        delta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        RefinedStorageUtils.writeClientStack(buf, network, stack);
        buf.writeInt(delta);
    }

    @Override
    public IMessage onMessage(MessageGridDelta message, MessageContext ctx) {
        for (ClientStack stack : RefinedStorage.INSTANCE.items) {
            if (stack.equals(message.clientStack)) {
                if (stack.getStack().stackSize + message.delta == 0 && !message.clientStack.isCraftable()) {
                    RefinedStorage.INSTANCE.items.remove(stack);
                } else {
                    stack.getStack().stackSize += message.delta;
                }

                return null;
            }
        }

        RefinedStorage.INSTANCE.items.add(message.clientStack);

        return null;
    }
}
