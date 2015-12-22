package storagecraft.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import storagecraft.tile.TileGrid;

public class MessageGridCraftingUpdate implements IMessage, IMessageHandler<MessageGridCraftingUpdate, IMessage>
{
	private int x;
	private int y;
	private int z;
	private ItemStack[] craftingMatrix = new ItemStack[9];

	public MessageGridCraftingUpdate()
	{
	}

	public MessageGridCraftingUpdate(TileGrid grid)
	{
		this.x = grid.xCoord;
		this.y = grid.yCoord;
		this.z = grid.zCoord;

		for (int i = 0; i < 9; ++i)
		{
			craftingMatrix[i] = grid.getCraftingMatrix().getStackInSlot(i);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();

		for (int i = 0; i < 9; ++i)
		{
			craftingMatrix[i] = ByteBufUtils.readItemStack(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);

		for (ItemStack stack : craftingMatrix)
		{
			ByteBufUtils.writeItemStack(buf, stack);
		}
	}

	@Override
	public IMessage onMessage(MessageGridCraftingUpdate message, MessageContext context)
	{
		TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z);

		if (tile instanceof TileGrid)
		{
			for (int i = 0; i < 9; ++i)
			{
				((TileGrid) tile).getCraftingMatrix().setInventorySlotContents(i, message.craftingMatrix[i]);
			}
		}

		return null;
	}
}
