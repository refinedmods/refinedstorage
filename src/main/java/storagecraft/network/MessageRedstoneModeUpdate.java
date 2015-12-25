package storagecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import storagecraft.tile.IRedstoneModeSetting;

public class MessageRedstoneModeUpdate implements IMessage, IMessageHandler<MessageRedstoneModeUpdate, IMessage>
{
	private int x;
	private int y;
	private int z;

	public MessageRedstoneModeUpdate()
	{
	}

	public MessageRedstoneModeUpdate(IRedstoneModeSetting setting)
	{
		this.x = setting.getPos().getX();
		this.y = setting.getPos().getY();
		this.z = setting.getPos().getZ();
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
	public IMessage onMessage(MessageRedstoneModeUpdate message, MessageContext context)
	{
		EntityPlayerMP player = context.getServerHandler().playerEntity;

		TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

		if (tile instanceof IRedstoneModeSetting)
		{
			IRedstoneModeSetting setting = (IRedstoneModeSetting) tile;

			setting.setRedstoneMode(setting.getRedstoneMode().next());
		}

		return null;
	}
}
