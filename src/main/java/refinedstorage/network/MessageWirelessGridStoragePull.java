package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.controller.TileController;

public class MessageWirelessGridStoragePull extends MessageHandlerPlayerToServer<MessageWirelessGridStoragePull> implements IMessage {
    private int controllerX;
    private int controllerY;
    private int controllerZ;
    private int id;
    private int flags;

    public MessageWirelessGridStoragePull() {
    }

    public MessageWirelessGridStoragePull(int controllerX, int controllerY, int controllerZ, int id, int flags) {
        this.controllerX = controllerX;
        this.controllerY = controllerY;
        this.controllerZ = controllerZ;
        this.id = id;
        this.flags = flags;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        controllerX = buf.readInt();
        controllerY = buf.readInt();
        controllerZ = buf.readInt();
        id = buf.readInt();
        flags = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(controllerX);
        buf.writeInt(controllerY);
        buf.writeInt(controllerZ);
        buf.writeInt(id);
        buf.writeInt(flags);
    }

    @Override
    public void handle(MessageWirelessGridStoragePull message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.controllerX, message.controllerY, message.controllerZ));

        if (tile instanceof TileController && ((TileController) tile).canRun()) {
            ((TileController) tile).getStorageHandler().onPull(message.id, message.flags, player);
        }
    }
}
