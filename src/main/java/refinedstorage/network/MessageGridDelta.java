package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.gui.grid.ClientStack;
import refinedstorage.gui.grid.GuiGrid;

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
        clientStack = new ClientStack(buf);
        delta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ClientStack.write(buf, network, stack);
        buf.writeInt(delta);
    }

    @Override
    public IMessage onMessage(MessageGridDelta message, MessageContext ctx) {
        Item item = message.clientStack.getStack().getItem();

        for (ClientStack stack : GuiGrid.ITEMS.get(item)) {
            if (stack.equals(message.clientStack)) {
                if (stack.getStack().stackSize + message.delta == 0 && !message.clientStack.isCraftable()) {
                    GuiGrid.ITEMS.remove(item, stack);
                } else {
                    stack.getStack().stackSize += message.delta;
                }

                GuiGrid.markForSorting();

                return null;
            }
        }

        GuiGrid.ITEMS.put(item, message.clientStack);
        GuiGrid.markForSorting();

        return null;
    }
}
