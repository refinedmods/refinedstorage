package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.grid.TileGrid;

public class MessageGridCraftingStart extends MessageHandlerPlayerToServer<MessageGridCraftingStart> implements IMessage {
    private int x;
    private int y;
    private int z;
    private int id;
    private int quantity;

    public MessageGridCraftingStart() {
    }

    public MessageGridCraftingStart(int x, int y, int z, int id, int quantity) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.quantity = quantity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        id = buf.readInt();
        quantity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(id);
        buf.writeInt(quantity);
    }

    @Override
    public void handle(MessageGridCraftingStart message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof TileGrid && ((TileGrid) tile).isConnected()) {
            ((TileGrid) tile).getController().getStorageHandler().onCraftingRequested(message.id, message.quantity);
        }
    }
}
