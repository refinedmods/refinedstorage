package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.grid.TileGrid;

public class MessageGridStoragePull extends MessageHandlerPlayerToServer<MessageGridStoragePull> implements IMessage {
    private int gridX;
    private int gridY;
    private int gridZ;
    private int id;
    private int flags;

    public MessageGridStoragePull() {
    }

    public MessageGridStoragePull(int gridX, int gridY, int gridZ, int id, int flags) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.gridZ = gridZ;
        this.id = id;
        this.flags = flags;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gridX = buf.readInt();
        gridY = buf.readInt();
        gridZ = buf.readInt();
        id = buf.readInt();
        flags = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(gridX);
        buf.writeInt(gridY);
        buf.writeInt(gridZ);
        buf.writeInt(id);
        buf.writeInt(flags);
    }

    @Override
    public void handle(MessageGridStoragePull message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.gridX, message.gridY, message.gridZ));

        if (tile instanceof TileGrid && ((TileGrid) tile).isConnected()) {
            ((TileGrid) tile).getController().getStorageHandler().handlePull(message.id, message.flags, player);
        }
    }
}
