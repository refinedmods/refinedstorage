package storagecraft.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import storagecraft.tile.TileDetector;

public class MessageDetectorAmountUpdate implements IMessage, IMessageHandler<MessageDetectorAmountUpdate, IMessage>
{
	private int x;
	private int y;
	private int z;
	private int amount;

	public MessageDetectorAmountUpdate()
	{
	}

	public MessageDetectorAmountUpdate(TileDetector detector, int amount)
	{
		this.x = detector.xCoord;
		this.y = detector.yCoord;
		this.z = detector.zCoord;
		this.amount = amount;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		amount = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(amount);
	}

	@Override
	public IMessage onMessage(MessageDetectorAmountUpdate message, MessageContext context)
	{
		EntityPlayerMP player = context.getServerHandler().playerEntity;

		TileEntity tile = player.worldObj.getTileEntity(message.x, message.y, message.z);

		if (tile instanceof TileDetector && message.amount >= 0)
		{
			((TileDetector) tile).setAmount(message.amount);
		}

		return null;
	}
}
