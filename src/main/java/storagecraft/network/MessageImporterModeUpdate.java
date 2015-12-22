package storagecraft.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import storagecraft.tile.TileImporter;

public class MessageImporterModeUpdate implements IMessage, IMessageHandler<MessageImporterModeUpdate, IMessage>
{
	private int x;
	private int y;
	private int z;

	public MessageImporterModeUpdate()
	{
	}

	public MessageImporterModeUpdate(TileImporter importer)
	{
		this.x = importer.xCoord;
		this.y = importer.yCoord;
		this.z = importer.zCoord;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	@Override
	public IMessage onMessage(MessageImporterModeUpdate message, MessageContext context)
	{
		EntityPlayerMP player = context.getServerHandler().playerEntity;

		TileEntity tile = player.worldObj.getTileEntity(message.x, message.y, message.z);

		if (tile instanceof TileImporter)
		{
			TileImporter importer = (TileImporter) tile;

			importer.setMode(importer.getMode() == TileImporter.MODE_WHITELIST ? TileImporter.MODE_BLACKLIST : TileImporter.MODE_WHITELIST);
		}

		return null;
	}
}
