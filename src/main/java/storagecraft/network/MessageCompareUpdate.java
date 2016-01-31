package storagecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import storagecraft.tile.settings.ICompareSetting;

public class MessageCompareUpdate extends MessageHandlerPlayerToServer<MessageCompareUpdate> implements IMessage
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
		this.x = setting.getMachinePos().getX();
		this.y = setting.getMachinePos().getY();
		this.z = setting.getMachinePos().getZ();
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
	public void handle(MessageCompareUpdate message, EntityPlayerMP player)
	{
		TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

		if (tile instanceof ICompareSetting)
		{
			((ICompareSetting) tile).setCompare(message.compare);
		}
	}
}
