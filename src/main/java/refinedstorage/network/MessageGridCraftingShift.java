package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.container.ContainerGrid;
import refinedstorage.tile.grid.TileGrid;

public class MessageGridCraftingShift extends MessageHandlerPlayerToServer<MessageGridCraftingShift> implements IMessage {
    private int x;
    private int y;
    private int z;

    public MessageGridCraftingShift() {
    }

    public MessageGridCraftingShift(TileGrid grid) {
        this.x = grid.getPos().getX();
        this.y = grid.getPos().getY();
        this.z = grid.getPos().getZ();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void handle(MessageGridCraftingShift message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

        if (tile instanceof TileGrid) {
            TileGrid grid = (TileGrid) tile;

            if (grid.isConnected() && player.openContainer instanceof ContainerGrid) {
                grid.onCraftedShift((ContainerGrid) player.openContainer, player);
            }
        }
    }
}
