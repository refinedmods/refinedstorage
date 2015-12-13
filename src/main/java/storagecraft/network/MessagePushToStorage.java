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
	private int x;
	private int y;
	private int z;
	private int slot;

	public MessagePushToStorage() {
	}

	public MessagePushToStorage(int x, int y, int z, int slot) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.slot = slot;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		slot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(slot);
	}

	@Override
	public IMessage onMessage(MessagePushToStorage message, MessageContext context) {
		EntityPlayerMP player = context.getServerHandler().playerEntity;

		TileEntity tile = player.worldObj.getTileEntity(message.x, message.y, message.z);

		if (tile instanceof TileController) {
			TileController controller = (TileController) tile;

			ItemStack stack = message.slot == -1 ? player.inventory.getItemStack() : player.inventory.getStackInSlot(message.slot);

			if (stack != null) {
				boolean success = controller.getStorage().push(stack);

				if (success) {
					if (message.slot == -1) {
						player.inventory.setItemStack(null);
						player.updateHeldItem();
					} else {
						player.inventory.setInventorySlotContents(message.slot, null);
					}
				}
			}
		}

		return null;
	}
}
