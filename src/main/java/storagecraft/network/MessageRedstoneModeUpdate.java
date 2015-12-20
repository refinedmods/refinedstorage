package storagecraft.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import storagecraft.tile.IRedstoneControllable;

public class MessageRedstoneModeUpdate implements IMessage, IMessageHandler<MessageRedstoneModeUpdate, IMessage> {
	private int x;
	private int y;
	private int z;

	public MessageRedstoneModeUpdate() {
	}

	public MessageRedstoneModeUpdate(IRedstoneControllable control) {
		this.x = control.getX();
		this.y = control.getY();
		this.z = control.getZ();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	@Override
	public IMessage onMessage(MessageRedstoneModeUpdate message, MessageContext context) {
		EntityPlayerMP player = context.getServerHandler().playerEntity;

		TileEntity tile = player.worldObj.getTileEntity(message.x, message.y, message.z);

		if (tile instanceof IRedstoneControllable) {
			IRedstoneControllable control = (IRedstoneControllable) tile;

			control.setRedstoneMode(control.getRedstoneMode().next());
		}

		return null;
	}
}
