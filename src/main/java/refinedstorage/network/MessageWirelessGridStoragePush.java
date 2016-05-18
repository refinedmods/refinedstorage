package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.controller.TileController;

public class MessageWirelessGridStoragePush extends MessageHandlerPlayerToServer<MessageWirelessGridStoragePush> implements IMessage {
    private int controllerX;
    private int controllerY;
    private int controllerZ;
    private int playerSlot;
    private boolean one;

    public MessageWirelessGridStoragePush() {
    }

    public MessageWirelessGridStoragePush(int controllerX, int controllerY, int controllerZ, int playerSlot, boolean one) {
        this.controllerX = controllerX;
        this.controllerY = controllerY;
        this.controllerZ = controllerZ;
        this.playerSlot = playerSlot;
        this.one = one;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        controllerX = buf.readInt();
        controllerY = buf.readInt();
        controllerZ = buf.readInt();
        playerSlot = buf.readInt();
        one = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(controllerX);
        buf.writeInt(controllerY);
        buf.writeInt(controllerZ);
        buf.writeInt(playerSlot);
        buf.writeBoolean(one);
    }

    @Override
    public void handle(MessageWirelessGridStoragePush message, EntityPlayerMP player) {
        TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.controllerX, message.controllerY, message.controllerZ));

        if (tile instanceof TileController && ((TileController) tile).canRun()) {
            ((TileController) tile).handleStoragePush(message.playerSlot, message.one, player);
        }
    }
}
