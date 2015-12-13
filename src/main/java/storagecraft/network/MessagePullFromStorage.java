package storagecraft.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import storagecraft.storage.StorageItem;
import storagecraft.tile.TileController;

public class MessagePullFromStorage implements IMessage, IMessageHandler<MessagePullFromStorage, IMessage> {
	private int x;
	private int y;
	private int z;
	// @TODO: this won't work when sorting
	private int slot;
	private boolean half;
	private boolean shift;

	public MessagePullFromStorage() {
	}

	public MessagePullFromStorage(int x, int y, int z, int slot, boolean half, boolean shift) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.slot = slot;
		this.half = half;
		this.shift = shift;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		slot = buf.readInt();
		half = buf.readBoolean();
		shift = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(slot);
		buf.writeBoolean(half);
		buf.writeBoolean(shift);
	}

	@Override
	public IMessage onMessage(MessagePullFromStorage message, MessageContext context) {
		EntityPlayerMP player = context.getServerHandler().playerEntity;

		TileEntity tile = player.worldObj.getTileEntity(message.x, message.y, message.z);

		if (tile instanceof TileController) {
			TileController controller = (TileController) tile;

			if (message.slot < controller.getStorage().getItems().size()) {
				StorageItem item = controller.getStorage().getItems().get(message.slot);

				int quantity = 64;

				if (message.half && item.getQuantity() > 1) {
					quantity = item.getQuantity() / 2;
				}

				ItemStack stack = controller.getStorage().take(item.getType(), quantity, item.getMeta());

				if (message.shift) {
					// @TODO: This doesn't work
					if (!player.inventory.addItemStackToInventory(stack.copy())) {
						controller.getStorage().push(stack);
					}
				} else {
					player.inventory.setItemStack(stack);
					player.updateHeldItem();
				}
			}
		}

		return null;
	}
}
