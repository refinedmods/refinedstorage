package storagecraft.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import storagecraft.tile.INetworkTile;

public class MessageTileUpdate implements IMessage, IMessageHandler<MessageTileUpdate, IMessage> {
	private TileEntity tile;
	private int x;
	private int y;
	private int z;

	public MessageTileUpdate() {
	}

	public MessageTileUpdate(TileEntity tile) {
		this.tile = tile;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();

		tile = Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);

		if (tile instanceof INetworkTile) {
			((INetworkTile) tile).fromBytes(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(tile.xCoord);
		buf.writeInt(tile.yCoord);
		buf.writeInt(tile.zCoord);

		if (tile instanceof INetworkTile) {
			((INetworkTile) tile).toBytes(buf);
		}
	}

	@Override
	public IMessage onMessage(MessageTileUpdate message, MessageContext ctx) {
		return null;
	}
}
