package storagecraft.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import storagecraft.tile.ICompareSetting;

public class MessageCompareUpdate implements IMessage, IMessageHandler<MessageCompareUpdate, IMessage>
{
	private int x;
	private int y;
	private int z;
	private int compare;

	public MessageCompareUpdate()
	{
	}

	public MessageCompareUpdate(ICompareSetting setting, int compare)
	{
		this.x = setting.getX();
		this.y = setting.getY();
		this.z = setting.getZ();
		this.compare = compare;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		compare = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(compare);
	}

	@Override
	public IMessage onMessage(MessageCompareUpdate message, MessageContext context)
	{
		EntityPlayerMP player = context.getServerHandler().playerEntity;

		TileEntity tile = player.worldObj.getTileEntity(message.x, message.y, message.z);

		if (tile instanceof ICompareSetting)
		{
			((ICompareSetting) tile).setCompare(message.compare);
		}

		return null;
	}
}
