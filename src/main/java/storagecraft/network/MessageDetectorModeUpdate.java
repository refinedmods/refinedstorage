package storagecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import storagecraft.tile.TileDetector;

public class MessageDetectorModeUpdate implements IMessage, IMessageHandler<MessageDetectorModeUpdate, IMessage>
{
	private int x;
	private int y;
	private int z;

	public MessageDetectorModeUpdate()
	{
	}

	public MessageDetectorModeUpdate(TileDetector detector)
	{
		this.x = detector.getPos().getX();
		this.y = detector.getPos().getY();
		this.z = detector.getPos().getZ();
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
	public IMessage onMessage(MessageDetectorModeUpdate message, MessageContext context)
	{
		EntityPlayerMP player = context.getServerHandler().playerEntity;

		TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

		if (tile instanceof TileDetector)
		{
			TileDetector detector = (TileDetector) tile;

			switch (detector.getMode())
			{
				case TileDetector.MODE_UNDER:
					detector.setMode(TileDetector.MODE_EQUAL);
					break;
				case TileDetector.MODE_EQUAL:
					detector.setMode(TileDetector.MODE_ABOVE);
					break;
				case TileDetector.MODE_ABOVE:
					detector.setMode(TileDetector.MODE_UNDER);
					break;
			}
		}

		return null;
	}
}
