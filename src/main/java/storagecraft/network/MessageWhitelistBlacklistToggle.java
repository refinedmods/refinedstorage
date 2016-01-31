package storagecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import storagecraft.tile.IWhitelistBlacklistSetting;

public class MessageWhitelistBlacklistToggle extends MessageHandlerPlayerToServer<MessageWhitelistBlacklistToggle> implements IMessage
{
	private int x;
	private int y;
	private int z;

	public MessageWhitelistBlacklistToggle()
	{
	}

	public MessageWhitelistBlacklistToggle(IWhitelistBlacklistSetting wb)
	{
		this.x = wb.getMachinePos().getX();
		this.y = wb.getMachinePos().getY();
		this.z = wb.getMachinePos().getZ();
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
	public void handle(MessageWhitelistBlacklistToggle message, EntityPlayerMP player)
	{
		TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

		if (tile instanceof IWhitelistBlacklistSetting)
		{
			IWhitelistBlacklistSetting wb = (IWhitelistBlacklistSetting) tile;

			if (wb.isWhitelist())
			{
				wb.setToBlacklist();
			}
			else if (wb.isBlacklist())
			{
				wb.setToWhitelist();
			}
		}
	}
}
