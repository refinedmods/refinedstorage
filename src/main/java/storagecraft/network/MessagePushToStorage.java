package storagecraft.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import storagecraft.tile.TileController;

public class MessagePushToStorage implements IMessage, IMessageHandler<MessagePushToStorage, IMessage> {
	private TileController controller;

	private int x;
	private int y;
	private int z;

	public MessagePushToStorage() {
	}

	public MessagePushToStorage(TileController controller) {
		this.controller = controller;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(controller.xCoord);
		buf.writeInt(controller.yCoord);
		buf.writeInt(controller.zCoord);
	}

	@Override
	public IMessage onMessage(MessagePushToStorage message, MessageContext context) {
		EntityPlayerMP player = context.getServerHandler().playerEntity;

		TileEntity tile = player.worldObj.getTileEntity(message.x, message.y, message.z);

		if (tile instanceof TileController) {
			controller = (TileController) tile;

			ItemStack stack = player.inventory.getItemStack();

			if (stack != null) {
				controller.getStorage().push(stack);

				player.inventory.setItemStack(null);
				player.updateHeldItem();
			}
		}

		return null;
	}
}
