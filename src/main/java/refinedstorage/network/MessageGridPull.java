package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.container.ContainerGrid;
import refinedstorage.tile.controller.StorageHandler;

public class MessageGridPull extends MessageHandlerPlayerToServer<MessageGridPull> implements IMessage {
    private ItemStack stack;
    private int flags;

    public MessageGridPull() {
    }

    public MessageGridPull(ItemStack stack, int flags) {
        this.stack = stack;
        this.flags = flags;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        stack = ByteBufUtils.readItemStack(buf);
        flags = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(flags);
    }

    @Override
    public void handle(MessageGridPull message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            StorageHandler handler = ((ContainerGrid) container).getGrid().getStorageHandler();

            if (handler != null) {
                handler.onPull(message.stack, message.flags, player);
            }
        }
    }
}
