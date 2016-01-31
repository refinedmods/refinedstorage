package storagecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import storagecraft.tile.TileStorage;

public class MessageStoragePriorityUpdate extends MessageHandlerPlayerToServer<MessageStoragePriorityUpdate> implements IMessage
{
	private int x;
	private int y;
	private int z;
	private int priority;

	public MessageStoragePriorityUpdate()
	{
	}

	public MessageStoragePriorityUpdate(TileStorage storage, int priority)
	{
		this.x = storage.getPos().getX();
		this.y = storage.getPos().getY();
		this.z = storage.getPos().getZ();
		this.priority = priority;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		priority = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(priority);
	}

	@Override
	public void handle(MessageStoragePriorityUpdate message, EntityPlayerMP player)
	{
		TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

		if (tile instanceof TileStorage)
		{
			((TileStorage) tile).setPriority(message.priority);
		}
	}
}
