package refinedstorage.tile;

import io.netty.buffer.ByteBuf;

public interface INetworkTile {
    void fromBytes(ByteBuf buf);

    void toBytes(ByteBuf buf);
}
