package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.api.storagenet.NetworkMaster;
import refinedstorage.api.storagenet.NetworkMasterRegistry;

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
        NetworkMaster network = NetworkMasterRegistry.get(new BlockPos(message.controllerX, message.controllerY, message.controllerZ), player.worldObj.provider.getDimension());

        if (network != null && network.canRun()) {
            network.getStorageHandler().onCraftingRequested(message.id, message.quantity);
        }
    }
}
