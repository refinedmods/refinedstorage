package storagecraft.tile;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import storagecraft.StorageCraft;
import storagecraft.network.MessageTileUpdate;

public abstract class TileBase extends TileEntity
{
	public static final int UPDATE_RANGE = 256;

	private ForgeDirection direction = ForgeDirection.UNKNOWN;

	protected int ticks;

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		ticks++;

		if (!worldObj.isRemote)
		{
			if (this instanceof INetworkTile)
			{
				TargetPoint target = new TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, UPDATE_RANGE);

				StorageCraft.NETWORK.sendToAllAround(new MessageTileUpdate(this), target);
			}
		}
	}

	public void setDirection(ForgeDirection direction)
	{
		this.direction = direction;
	}

	public ForgeDirection getDirection()
	{
		return direction;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		direction = ForgeDirection.getOrientation(nbt.getInteger("Direction"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		nbt.setInteger("Direction", direction.ordinal());
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setInteger("Direction", direction.ordinal());

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
	{
		direction = ForgeDirection.getOrientation(packet.func_148857_g().getInteger("Direction"));
	}

	public IInventory getDroppedInventory()
	{
		return null;
	}
}
