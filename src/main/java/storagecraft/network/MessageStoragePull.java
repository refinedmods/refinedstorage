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

public class MessageStoragePull implements IMessage, IMessageHandler<MessageStoragePull, IMessage> {
	private int x;
	private int y;
	private int z;
	private int id;
	private boolean half;
	private boolean shift;

	public MessageStoragePull() {
	}

	public MessageStoragePull(int x, int y, int z, int id, boolean half, boolean shift) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
		this.half = half;
		this.shift = shift;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		id = buf.readInt();
		half = buf.readBoolean();
		shift = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
		buf.writeBoolean(half);
		buf.writeBoolean(shift);
	}

	@Override
	public IMessage onMessage(MessageStoragePull message, MessageContext context) {
		EntityPlayerMP player = context.getServerHandler().playerEntity;

		TileEntity tile = player.worldObj.getTileEntity(message.x, message.y, message.z);

		if (tile instanceof TileController) {
			TileController controller = (TileController) tile;

			if (message.id < controller.getItems().size()) {
				StorageItem item = controller.getItems().get(message.id);

				int quantity = 64;

				if (message.half && item.getQuantity() > 1) {
					quantity = item.getQuantity() / 2;

					if (quantity > 64) {
						quantity = 64;
					}
				}

				ItemStack took = controller.take(item.copy(quantity).toItemStack());

				if (took != null) {
					if (message.shift) {
						if (!player.inventory.addItemStackToInventory(took.copy())) {
							controller.push(took);
						}
					} else {
						player.inventory.setItemStack(took);
						player.updateHeldItem();
					}
				}
			}
		}

		return null;
	}
}
