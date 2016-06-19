package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.api.storagenet.StorageNetwork;
import refinedstorage.api.storagenet.StorageNetworkRegistry;

public class MessageGridStoragePull extends MessageHandlerPlayerToServer<MessageGridStoragePull> implements IMessage {
    private int x;
    private int y;
    private int z;
    private int id;
    private int flags;

    public MessageGridStoragePull() {
    }

    public MessageGridStoragePull(int x, int y, int z, int id, int flags) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.flags = flags;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        id = buf.readInt();
        flags = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(id);
        buf.writeInt(flags);
    }

    @Override
    public void handle(MessageGridStoragePull message, EntityPlayerMP player) {
        StorageNetwork network = StorageNetworkRegistry.NETWORKS.get(new BlockPos(message.x, message.y, message.z));

        if (network != null && network.canRun()) {
            network.getStorageHandler().onPull(message.id, message.flags, player);
        }
    }
}
