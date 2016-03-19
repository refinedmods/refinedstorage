package storagecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import storagecraft.tile.TileDetector;

public class MessageDetectorAmountUpdate extends MessageHandlerPlayerToServer<MessageDetectorAmountUpdate> implements IMessage
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
		this.x = detector.getPos().getX();
		this.y = detector.getPos().getY();
		this.z = detector.getPos().getZ();
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
	public void handle(MessageDetectorAmountUpdate message, EntityPlayerMP player)
	{
		TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

		if (tile instanceof TileDetector && message.amount >= 0)
		{
			((TileDetector) tile).setAmount(message.amount);
		}
	}
}
