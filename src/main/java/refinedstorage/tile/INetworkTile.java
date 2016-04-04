package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;

public interface INetworkTile {
    void receiveData(ByteBuf buf);

    void sendData(ByteBuf buf);

    void receiveContainerData(ByteBuf buf);

    void sendContainerData(ByteBuf buf);

    Class<? extends Container> getContainer();
}
