package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import storagecraft.inventory.InventorySimple;
import storagecraft.item.ItemWirelessGrid;
import storagecraft.util.InventoryUtils;

public class TileWirelessTransmitter extends TileMachine implements IInventory
{
	public static final int TOTAL_PROGRESS = 10000;

	public static final String NBT_WORKING = "Working";
	public static final String NBT_PROGRESS = "Progress";

	private InventorySimple inventory = new InventorySimple("wirelessTransmitter", 3);

	private boolean working;
	private int progress = 0;

	@Override
	public int getEnergyUsage()
	{
		return 4;
	}

	@Override
	public void updateMachine()
	{
		if (working)
		{
			progress++;

			if (progress == TOTAL_PROGRESS)
			{
				reset();
			}
		}
		else if (inventory.getStackInSlot(0) != null)
		{
			inventory.decrStackSize(0, 1);

			progress = 0;
			working = true;
		}

		if (inventory.getStackInSlot(1) != null)
		{
			ItemStack slot = inventory.getStackInSlot(1);

			slot.stackTagCompound = new NBTTagCompound();
			slot.stackTagCompound.setInteger(ItemWirelessGrid.NBT_WIRELESS_TRANSMITTER_X, xCoord);
			slot.stackTagCompound.setInteger(ItemWirelessGrid.NBT_WIRELESS_TRANSMITTER_Y, yCoord);
			slot.stackTagCompound.setInteger(ItemWirelessGrid.NBT_WIRELESS_TRANSMITTER_Z, zCoord);

			inventory.setInventorySlotContents(2, slot);
			inventory.setInventorySlotContents(1, null);
		}
	}

	public void reset()
	{
		progress = 0;
		working = false;
	}

	@Override
	public void onDisconnected()
	{
		super.onDisconnected();

		reset();
	}

	public boolean isWorking()
	{
		return working;
	}

	public int getProgress()
	{
		return progress;
	}

	public TileGrid getGrid(int type)
	{
		for (TileMachine machine : getController().getMachines())
		{
			if (worldObj.getTileEntity(machine.xCoord, machine.yCoord, machine.zCoord) != null)
			{
				if (machine instanceof TileGrid)
				{
					TileGrid grid = (TileGrid) machine;

					if (grid.getType() == type)
					{
						return grid;
					}
				}
			}
		}

		return null;
	}

	@Override
	public int getSizeInventory()
	{
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		return inventory.decrStackSize(slot, amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName()
	{
		return inventory.getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return inventory.hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit()
	{
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return inventory.isUseableByPlayer(player);
	}

	@Override
	public void openInventory()
	{
		inventory.openInventory();
	}

	@Override
	public void closeInventory()
	{
		inventory.closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		InventoryUtils.restoreInventory(this, nbt);

		if (nbt.hasKey(NBT_WORKING))
		{
			working = nbt.getBoolean(NBT_WORKING);
		}

		if (nbt.hasKey(NBT_PROGRESS))
		{
			progress = nbt.getInteger(NBT_PROGRESS);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		InventoryUtils.saveInventory(this, nbt);

		nbt.setBoolean(NBT_WORKING, working);
		nbt.setInteger(NBT_PROGRESS, progress);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);

		working = buf.readBoolean();
		progress = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);

		buf.writeBoolean(working);
		buf.writeInt(progress);
	}
}
