package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.gui.grid.GuiGrid;
import refinedstorage.gui.grid.stack.ClientStackItem;
import refinedstorage.gui.grid.stack.IClientStack;

public class MessageGridDelta implements IMessage, IMessageHandler<MessageGridDelta, IMessage> {
    private INetworkMaster network;
    private ItemStack stack;
    private int delta;

    private ClientStackItem clientStack;

    public MessageGridDelta() {
    }

    public MessageGridDelta(INetworkMaster network, ItemStack stack, int delta) {
        this.network = network;
        this.stack = stack;
        this.delta = delta;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        clientStack = new ClientStackItem(buf);
        delta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ClientStackItem.write(buf, network, stack);
        buf.writeInt(delta);
    }

    @Override
    public IMessage onMessage(MessageGridDelta message, MessageContext ctx) {
        Item item = message.clientStack.getStack().getItem();

        for (IClientStack anyStack : GuiGrid.ITEMS.get(item)) {
            ClientStackItem stack = (ClientStackItem) anyStack;

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
