package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;

public interface INetworkTile
{
	public void fromBytes(ByteBuf buf);

	public void toBytes(ByteBuf buf);

	public BlockPos getTilePos();
}
