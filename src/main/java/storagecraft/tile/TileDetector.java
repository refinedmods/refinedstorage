package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import storagecraft.StorageCraftBlocks;
import storagecraft.inventory.InventorySimple;
import storagecraft.storage.StorageItem;
import storagecraft.util.InventoryUtils;

public class TileDetector extends TileMachine implements IInventory, ISidedInventory, ICompareSetting
{
	public static final int MODE_UNDER = 0;
	public static final int MODE_EQUAL = 1;
	public static final int MODE_ABOVE = 2;

	public static final String NBT_COMPARE = "Compare";
	public static final String NBT_MODE = "Mode";
	public static final String NBT_AMOUNT = "Amount";

	private InventorySimple inventory = new InventorySimple("detector", 1);

	private int compare = 0;
	private int mode = MODE_EQUAL;
	private int amount = 0;

	private boolean powered = false;

	public TileDetector()
	{
		this.redstoneControlled = false;
	}

	@Override
	public int getEnergyUsage()
	{
		return 4;
	}

	@Override
	public void updateMachine()
	{
		if (ticks % 5 == 0)
		{
			ItemStack slot = inventory.getStackInSlot(0);

			boolean lastPowered = powered;

			if (slot != null)
			{
				boolean foundAny = false;

				for (StorageItem item : getController().getItems())
				{
					if (item.compare(slot, compare))
					{
						foundAny = true;

						switch (mode)
						{
							case MODE_UNDER:
								powered = item.getQuantity() < amount;
								break;
							case MODE_EQUAL:
								powered = item.getQuantity() == amount;
								break;
							case MODE_ABOVE:
								powered = item.getQuantity() > amount;
								break;
						}

						break;
					}
				}

				if (!foundAny)
				{
					if (mode == MODE_UNDER && amount != 0)
					{
						powered = true;
					}
					else if (mode == MODE_EQUAL && amount == 0)
					{
						powered = true;
					}
					else
					{
						powered = false;
					}
				}
			}
			else
			{
				powered = false;
			}

			if (powered != lastPowered)
			{
				worldObj.markBlockForUpdate(pos);
				worldObj.notifyNeighborsOfStateChange(pos, StorageCraftBlocks.DETECTOR);
			}
		}
	}

	public boolean isPowered()
	{
		return powered;
	}

	@Override
	public int getCompare()
	{
		return compare;
	}

	@Override
	public void setCompare(int compare)
	{
		markDirty();

		this.compare = compare;
	}

	public int getMode()
	{
		return mode;
	}

	public void setMode(int mode)
	{
		markDirty();

		this.mode = mode;
	}

	public int getAmount()
	{
		return amount;
	}

	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		if (nbt.hasKey(NBT_COMPARE))
		{
			compare = nbt.getInteger(NBT_COMPARE);
		}

		if (nbt.hasKey(NBT_MODE))
		{
			mode = nbt.getInteger(NBT_MODE);
		}

		if (nbt.hasKey(NBT_AMOUNT))
		{
			amount = nbt.getInteger(NBT_AMOUNT);
		}

		InventoryUtils.restoreInventory(this, nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		nbt.setInteger(NBT_COMPARE, compare);
		nbt.setInteger(NBT_MODE, mode);
		nbt.setInteger(NBT_AMOUNT, amount);

		InventoryUtils.saveInventory(this, nbt);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);

		compare = buf.readInt();
		mode = buf.readInt();
		amount = buf.readInt();

		boolean lastPowered = powered;

		powered = buf.readBoolean();

		if (powered != lastPowered)
		{
			worldObj.markBlockForUpdate(pos);
			worldObj.notifyNeighborsOfStateChange(pos, StorageCraftBlocks.DETECTOR);
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);

		buf.writeInt(compare);
		buf.writeInt(mode);
		buf.writeInt(amount);
		buf.writeBoolean(powered);
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
	public ItemStack removeStackFromSlot(int slot)
	{
		return inventory.removeStackFromSlot(slot);
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
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		return inventory.isItemValidForSlot(slot, stack);
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

	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return new int[]
			{
			};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing direction)
	{
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		return false;
	}
}
