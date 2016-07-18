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

import java.util.ArrayList;
import java.util.List;

public class MessageGridUpdate implements IMessage, IMessageHandler<MessageGridUpdate, IMessage> {
    private INetworkMaster network;
    private List<ClientStack> items = new ArrayList<ClientStack>();

    public MessageGridUpdate() {
    }

    public MessageGridUpdate(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int items = buf.readInt();

        for (int i = 0; i < items; ++i) {
            this.items.add(RefinedStorageUtils.readClientStack(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(network.getStorage().getStacks().size());

        for (ItemStack stack : network.getStorage().getStacks()) {
            RefinedStorageUtils.writeClientStack(buf, network, stack);
        }
    }

    @Override
    public IMessage onMessage(MessageGridUpdate message, MessageContext ctx) {
        RefinedStorage.INSTANCE.items = message.items;

        return null;
    }
}
