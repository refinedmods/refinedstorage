package storagecraft.tile;

import io.netty.buffer.ByteBuf;

public interface INetworkTile {
	public void fromBytes(ByteBuf buf);

	public void toBytes(ByteBuf buf);
}
