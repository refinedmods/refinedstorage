package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.tile.TileGrid;

public class MessageGridSortingUpdate extends MessageHandlerPlayerToServer<MessageGridSortingUpdate> implements IMessage
{
	private int x;
	private int y;
	private int z;
	private int sortingDirection;
	private int sortingType;

	public MessageGridSortingUpdate()
	{
	}

	public MessageGridSortingUpdate(TileGrid grid, int sortingDirection, int sortingType)
	{
		this.x = grid.getPos().getX();
		this.y = grid.getPos().getY();
		this.z = grid.getPos().getZ();
		this.sortingDirection = sortingDirection;
		this.sortingType = sortingType;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		sortingDirection = buf.readInt();
		sortingType = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(sortingDirection);
		buf.writeInt(sortingType);
	}

	@Override
	public void handle(MessageGridSortingUpdate message, EntityPlayerMP player)
	{
		TileEntity tile = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));

		if (tile instanceof TileGrid)
		{
			if (message.sortingDirection == TileGrid.SORTING_DIRECTION_ASCENDING || message.sortingDirection == TileGrid.SORTING_DIRECTION_DESCENDING)
			{
				((TileGrid) tile).setSortingDirection(message.sortingDirection);
			}

			if (message.sortingType == TileGrid.SORTING_TYPE_QUANTITY || message.sortingType == TileGrid.SORTING_TYPE_NAME)
			{
				((TileGrid) tile).setSortingType(message.sortingType);
			}
		}
	}
}
