package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.grid.TileGrid;

public class MessageGridHeldItemPush extends MessageHandlerPlayerToServer<MessageGridHeldItemPush> implements IMessage {
    private int x;
    private int y;
    private int z;
    private boolean one;

    public MessageGridHeldItemPush() {
    }

    public MessageGridHeldItemPush(int x, int y, int z, boolean one) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.one = one;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        one = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(one);
    }

    @Override
    public void handle(MessageGridHeldItemPush message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof TileGrid && ((TileGrid) tile).isConnected()) {
            ((TileGrid) tile).getNetwork().getStorageHandler().onHeldItemPush(message.one, player);
        }
    }
}
