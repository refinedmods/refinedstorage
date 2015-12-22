package storagecraft.tile;

import io.netty.buffer.ByteBuf;

public interface INetworkTile
{
	public void fromBytes(ByteBuf buf);

	public void toBytes(ByteBuf buf);

	public int getX();

	public int getY();

	public int getZ();
}
