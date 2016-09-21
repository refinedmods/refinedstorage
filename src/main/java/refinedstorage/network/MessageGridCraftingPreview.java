package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.RefinedStorage;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.apiimpl.autocrafting.AutoCraftInfoStack;
import refinedstorage.apiimpl.autocrafting.CraftingUtils;
import refinedstorage.container.ContainerGrid;

import java.util.Collection;

public class MessageGridCraftingPreview extends MessageHandlerPlayerToServer<MessageGridCraftingPreview> implements IMessage {
    private int hash;
    private int quantity;

    public MessageGridCraftingPreview() {
    }

    public MessageGridCraftingPreview(int hash, int quantity) {
        this.hash = hash;
        this.quantity = quantity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hash = buf.readInt();
        quantity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hash);
        buf.writeInt(quantity);
    }

    @Override
    public void handle(MessageGridCraftingPreview message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            TileEntity entity = player.getEntityWorld().getTileEntity(((ContainerGrid) container).getGrid().getNetworkPosition());
            if (entity != null && entity instanceof INetworkMaster) {
                INetworkMaster network = (INetworkMaster) entity;
                Collection<AutoCraftInfoStack> stacks = CraftingUtils.getItems(network, network.getItemStorage().get(message.hash), message.quantity);
                RefinedStorage.INSTANCE.network.sendTo(new MessageGridCraftingPreviewResponse(stacks, message.hash, message.quantity), player);
            }
        }
    }
}
