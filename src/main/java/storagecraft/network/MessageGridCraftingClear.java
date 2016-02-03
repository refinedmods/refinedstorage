package storagecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import storagecraft.block.EnumGridType;
import storagecraft.tile.TileGrid;

public class MessageGridCraftingClear extends MessageHandlerPlayerToServer<MessageGridCraftingClear> implements IMessage
{
	private int x;
	private int y;
	private int z;

	public MessageGridCraftingClear()
	{
	}

	public MessageGridCraftingClear(TileGrid grid)
	{
		this.x = grid.getPos().getX();
		this.y = grid.getPos().getY();
		this.z = grid.getPos().getZ();
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
	public void handle(MessageGridCraftingClear message, EntityPlayerMP player)
	{
		TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

		if (tile instanceof TileGrid)
		{
			TileGrid grid = (TileGrid) tile;

			if (grid.getType() == EnumGridType.PATTERN)
			{
				for (int i = 0; i < 9; ++i)
				{
					grid.getPatternCraftingInventory().setInventorySlotContents(i, null);
				}
			}
			else if (grid.isConnected() && grid.getType() == EnumGridType.CRAFTING)
			{
				for (int i = 0; i < grid.getCraftingInventory().getSizeInventory(); ++i)
				{
					ItemStack slot = grid.getCraftingInventory().getStackInSlot(i);

					if (slot != null)
					{
						if (grid.getController().push(slot))
						{
							grid.getCraftingInventory().setInventorySlotContents(i, null);
						}
					}
				}
			}
		}
	}
}
