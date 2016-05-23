package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.proxy.ClientProxy;
import refinedstorage.tile.ISynchronizedContainer;

public class MessageTileContainerUpdate implements IMessage, IMessageHandler<MessageTileContainerUpdate, IMessage> {
    private TileEntity tile;
    private int x;
    private int y;
    private int z;

    public MessageTileContainerUpdate() {
    }

    public MessageTileContainerUpdate(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();

        if (Minecraft.getMinecraft().theWorld != null) {
            tile = ClientProxy.getWorld().getTileEntity(new BlockPos(x, y, z));

            if (tile instanceof ISynchronizedContainer) {
                ((ISynchronizedContainer) tile).readContainerData(buf);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(tile.getPos().getX());
        buf.writeInt(tile.getPos().getY());
        buf.writeInt(tile.getPos().getZ());

        if (tile instanceof ISynchronizedContainer) {
            ((ISynchronizedContainer) tile).writeContainerData(buf);
        }
    }

    @Override
    public IMessage onMessage(MessageTileContainerUpdate message, MessageContext ctx) {
        return null;
    }
}
