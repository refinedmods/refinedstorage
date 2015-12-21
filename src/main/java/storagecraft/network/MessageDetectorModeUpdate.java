package storagecraft.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import storagecraft.tile.TileDetector;

public class MessageDetectorModeUpdate implements IMessage, IMessageHandler<MessageDetectorModeUpdate, IMessage> {
	private int x;
	private int y;
	private int z;
	private int amount;

	public MessageDetectorModeUpdate() {
	}

	public MessageDetectorModeUpdate(TileDetector detector, int amount) {
		this.x = detector.xCoord;
		this.y = detector.yCoord;
		this.z = detector.zCoord;
		this.amount = amount;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		amount = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(amount);
	}

	@Override
	public IMessage onMessage(MessageDetectorModeUpdate message, MessageContext context) {
		EntityPlayerMP player = context.getServerHandler().playerEntity;

		TileEntity tile = player.worldObj.getTileEntity(message.x, message.y, message.z);

		if (tile instanceof TileDetector) {
			TileDetector detector = (TileDetector) tile;

			switch (detector.getMode()) {
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

			if (message.amount >= 0) {
				detector.setAmount(message.amount);
			}
		}

		return null;
	}
}
