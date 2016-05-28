package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.grid.TileGrid;

public class MessageGridStoragePush extends MessageHandlerPlayerToServer<MessageGridStoragePush> implements IMessage {
    private int x;
    private int y;
    private int z;
    private int playerSlot;
    private boolean one;

    public MessageGridStoragePush() {
    }

    public MessageGridStoragePush(int x, int y, int z, int playerSlot, boolean one) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.playerSlot = playerSlot;
        this.one = one;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        playerSlot = buf.readInt();
        one = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(playerSlot);
        buf.writeBoolean(one);
    }

    @Override
    public void handle(MessageGridStoragePush message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof TileGrid && ((TileGrid) tile).isConnected()) {
            ((TileGrid) tile).getController().getStorageHandler().handlePush(message.playerSlot, message.one, player);
        }
    }
}
