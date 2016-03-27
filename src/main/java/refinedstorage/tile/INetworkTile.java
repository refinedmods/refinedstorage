package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public interface INetworkTile {
    void fromBytes(ByteBuf buf);

    void toBytes(ByteBuf buf);

    BlockPos getTilePos();
}
