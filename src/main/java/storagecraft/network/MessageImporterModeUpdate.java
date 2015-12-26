package storagecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import storagecraft.tile.TileImporter;

public class MessageImporterModeUpdate extends MessageHandlerPlayerToServer<MessageImporterModeUpdate> implements IMessage
{
	private int x;
	private int y;
	private int z;

	public MessageImporterModeUpdate()
	{
	}

	public MessageImporterModeUpdate(TileImporter importer)
	{
		this.x = importer.getPos().getX();
		this.y = importer.getPos().getY();
		this.z = importer.getPos().getZ();
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
	public void handle(MessageImporterModeUpdate message, EntityPlayerMP player)
	{
		TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

		if (tile instanceof TileImporter)
		{
			TileImporter importer = (TileImporter) tile;

			importer.setMode(importer.getMode() == TileImporter.MODE_WHITELIST ? TileImporter.MODE_BLACKLIST : TileImporter.MODE_WHITELIST);
		}
	}
}
