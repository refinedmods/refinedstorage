package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import storagecraft.block.EnumGridType;
import storagecraft.inventory.InventorySimple;
import storagecraft.item.ItemWirelessGrid;
import storagecraft.util.InventoryUtils;

public class TileWirelessTransmitter extends TileMachine implements IInventory
{
	public static final int TOTAL_PROGRESS = 10000;

	public static final String NBT_WORKING = "Working";
	public static final String NBT_PROGRESS = "Progress";

	private InventorySimple inventory = new InventorySimple("wirelessTransmitter", 3);

	private boolean working = false;
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
		} else if (inventory.getStackInSlot(0) != null)
		{
			inventory.decrStackSize(0, 1);

			progress = 0;
			working = true;

			markDirty();
		}

		if (inventory.getStackInSlot(1) != null)
		{
			ItemStack slot = inventory.getStackInSlot(1);

			slot.setTagCompound(new NBTTagCompound());

			slot.getTagCompound().setInteger(ItemWirelessGrid.NBT_WIRELESS_TRANSMITTER_X, pos.getX());
			slot.getTagCompound().setInteger(ItemWirelessGrid.NBT_WIRELESS_TRANSMITTER_Y, pos.getY());
			slot.getTagCompound().setInteger(ItemWirelessGrid.NBT_WIRELESS_TRANSMITTER_Z, pos.getZ());

			inventory.setInventorySlotContents(2, slot);
			inventory.setInventorySlotContents(1, null);
		}
	}

	public void reset()
	{
		progress = 0;
		working = false;

		markDirty();
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

	public TileGrid getGrid(EnumGridType type)
	{
		for (TileMachine machine : getController().getMachines())
		{
			if (worldObj.getTileEntity(machine.getPos()) != null)
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

		boolean lastWorking = working;

		working = buf.readBoolean();
		progress = buf.readInt();

		if (lastWorking != working)
		{
			worldObj.markBlockForUpdate(pos);
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);

		buf.writeBoolean(working);
		buf.writeInt(progress);
	}

	@Override
	public IInventory getDroppedInventory()
	{
		return inventory;
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
	public ItemStack decrStackSize(int slot, int count)
	{
		return inventory.decrStackSize(slot, count);
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
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		inventory.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		inventory.closeInventory(player);
	}

	@Override
	public int getField(int id)
	{
		return inventory.getField(id);
	}

	@Override
	public void setField(int id, int value)
	{
		inventory.setField(id, value);
	}

	@Override
	public int getFieldCount()
	{
		return inventory.getFieldCount();
	}

	@Override
	public void clear()
	{
		inventory.clear();
	}

	@Override
	public String getName()
	{
		return inventory.getName();
	}

	@Override
	public boolean hasCustomName()
	{
		return inventory.hasCustomName();
	}

	@Override
	public IChatComponent getDisplayName()
	{
		return inventory.getDisplayName();
	}
}
