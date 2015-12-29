package storagecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import storagecraft.tile.INetworkTile;

public class MessageTileUpdate implements IMessage, IMessageHandler<MessageTileUpdate, IMessage>
{
	private TileEntity tile;
	private int x;
	private int y;
	private int z;

	public MessageTileUpdate()
	{
	}

	public MessageTileUpdate(TileEntity tile)
	{
		this.tile = tile;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();

		if (Minecraft.getMinecraft().theWorld != null)
		{
			tile = Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(x, y, z));

			if (tile instanceof INetworkTile)
			{
				((INetworkTile) tile).fromBytes(buf);
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(tile.getPos().getX());
		buf.writeInt(tile.getPos().getY());
		buf.writeInt(tile.getPos().getZ());

		if (tile instanceof INetworkTile)
		{
			((INetworkTile) tile).toBytes(buf);
		}
	}

	@Override
	public IMessage onMessage(MessageTileUpdate message, MessageContext ctx)
	{
		return null;
	}
}
