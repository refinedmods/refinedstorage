package storagecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
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
		this.x = grid.getPos().getX();
		this.y = grid.getPos().getY();
		this.z = grid.getPos().getZ();

		for (int i = 0; i < 9; ++i)
		{
			craftingMatrix[i] = grid.getCraftingInventory().getStackInSlot(i);
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
	public IMessage onMessage(final MessageGridCraftingUpdate message, MessageContext context)
	{
		Minecraft.getMinecraft().addScheduledTask(new Runnable()
		{
			@Override
			public void run()
			{
				TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(message.x, message.y, message.z));

				if (tile instanceof TileGrid)
				{
					for (int i = 0; i < 9; ++i)
					{
						((TileGrid) tile).getCraftingInventory().setInventorySlotContents(i, message.craftingMatrix[i]);
					}
				}
			}
		});

		return null;
	}
}
