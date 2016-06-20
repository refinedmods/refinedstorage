package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.api.network.NetworkMaster;
import refinedstorage.api.network.NetworkMasterRegistry;

public class MessageWirelessGridHeldItemPush extends MessageHandlerPlayerToServer<MessageWirelessGridHeldItemPush> implements IMessage {
    private int controllerX;
    private int controllerY;
    private int controllerZ;
    private boolean one;

    public MessageWirelessGridHeldItemPush() {
    }

    public MessageWirelessGridHeldItemPush(int controllerX, int controllerY, int controllerZ, boolean one) {
        this.controllerX = controllerX;
        this.controllerY = controllerY;
        this.controllerZ = controllerZ;
        this.one = one;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        controllerX = buf.readInt();
        controllerY = buf.readInt();
        controllerZ = buf.readInt();
        one = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(controllerX);
        buf.writeInt(controllerY);
        buf.writeInt(controllerZ);
        buf.writeBoolean(one);
    }

    @Override
    public void handle(MessageWirelessGridHeldItemPush message, EntityPlayerMP player) {
        NetworkMaster network = NetworkMasterRegistry.get(new BlockPos(message.controllerX, message.controllerY, message.controllerZ), player.worldObj.provider.getDimension());

        if (network != null && network.canRun()) {
            network.getStorageHandler().onHeldItemPush(message.one, player);
        }
    }
}
