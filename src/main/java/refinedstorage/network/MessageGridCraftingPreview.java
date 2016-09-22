package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.RefinedStorage;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.apiimpl.autocrafting.preview.CraftingPreviewData;
import refinedstorage.container.ContainerGrid;

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
            TileEntity tile = player.getEntityWorld().getTileEntity(((ContainerGrid) container).getGrid().getNetworkPosition());

            if (tile != null && tile instanceof INetworkMaster) {
                INetworkMaster network = (INetworkMaster) tile;

                ItemStack stack = network.getItemStorage().get(message.hash);

                if (stack != null) {
                    CraftingPreviewData previewData = new CraftingPreviewData(network);

                    previewData.calculate(stack, message.quantity);

                    RefinedStorage.INSTANCE.network.sendTo(new MessageGridCraftingPreviewResponse(previewData.values(), message.hash, message.quantity), player);
                }
            }
        }
    }
}
