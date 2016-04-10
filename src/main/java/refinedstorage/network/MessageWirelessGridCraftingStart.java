package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.TileController;

public class MessageWirelessGridCraftingStart extends MessageHandlerPlayerToServer<MessageWirelessGridCraftingStart> implements IMessage {
    private int controllerX;
    private int controllerY;
    private int controllerZ;
    private int id;
    private int quantity;

    public MessageWirelessGridCraftingStart() {
    }

    public MessageWirelessGridCraftingStart(int controllerX, int controllerY, int controllerZ, int id, int quantity) {
        this.controllerX = controllerX;
        this.controllerY = controllerY;
        this.controllerZ = controllerZ;
        this.id = id;
        this.quantity = quantity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        controllerX = buf.readInt();
        controllerY = buf.readInt();
        controllerZ = buf.readInt();
        id = buf.readInt();
        quantity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(controllerX);
        buf.writeInt(controllerY);
        buf.writeInt(controllerZ);
        buf.writeInt(id);
        buf.writeInt(quantity);
    }

    @Override
    public void handle(MessageWirelessGridCraftingStart message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.controllerX, message.controllerY, message.controllerZ));

        if (tile instanceof TileController && ((TileController) tile).isActive()) {
            ((TileController) tile).onCraftingRequested(message.id, message.quantity);
        }
    }
}
