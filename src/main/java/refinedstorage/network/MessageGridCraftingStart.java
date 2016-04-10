package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.grid.TileGrid;

public class MessageGridCraftingStart extends MessageHandlerPlayerToServer<MessageGridCraftingStart> implements IMessage {
    private int gridX;
    private int gridY;
    private int gridZ;
    private int id;
    private int quantity;

    public MessageGridCraftingStart() {
    }

    public MessageGridCraftingStart(int gridX, int gridY, int gridZ, int id, int quantity) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.gridZ = gridZ;
        this.id = id;
        this.quantity = quantity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gridX = buf.readInt();
        gridY = buf.readInt();
        gridZ = buf.readInt();
        id = buf.readInt();
        quantity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(gridX);
        buf.writeInt(gridY);
        buf.writeInt(gridZ);
        buf.writeInt(id);
        buf.writeInt(quantity);
    }

    @Override
    public void handle(MessageGridCraftingStart message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.gridX, message.gridY, message.gridZ));

        if (tile instanceof TileGrid && ((TileGrid) tile).isConnected() && message.quantity > 0 && message.id >= 0) {
            ((TileGrid) tile).getController().onCraftingRequested(message.id, message.quantity);
        }
    }
}
