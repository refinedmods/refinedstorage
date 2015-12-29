package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import storagecraft.inventory.InventorySimple;
import storagecraft.util.InventoryUtils;

public class TileImporter extends TileMachine implements IInventory, ISidedInventory, ICompareSetting
{
	public static final int MODE_WHITELIST = 0;
	public static final int MODE_BLACKLIST = 1;

	public static final String NBT_COMPARE = "Compare";
	public static final String NBT_MODE = "Mode";

	private InventorySimple inventory = new InventorySimple("importer", 9);

	private int compare = 0;
	private int mode = MODE_WHITELIST;

	private int currentSlot = 0;

	@Override
	public int getEnergyUsage()
	{
		return 2;
	}

	@Override
	public void updateMachine()
	{
		TileEntity tile = worldObj.getTileEntity(pos.offset(getDirection()));

		if (tile instanceof IInventory)
		{
			IInventory connectedInventory = (IInventory) tile;

			if (ticks % 5 == 0)
			{
				ItemStack slot = connectedInventory.getStackInSlot(currentSlot);

				while ((slot = connectedInventory.getStackInSlot(currentSlot)) == null)
				{
					currentSlot++;

					if (currentSlot > connectedInventory.getSizeInventory() - 1)
					{
						break;
					}
				}

				if (slot != null && canImport(slot))
				{
					if (connectedInventory instanceof ISidedInventory)
					{
						ISidedInventory sided = (ISidedInventory) connectedInventory;

						if (sided.canExtractItem(currentSlot, slot.copy(), getDirection().getOpposite()))
						{
							if (getController().push(slot.copy()))
							{
								connectedInventory.setInventorySlotContents(currentSlot, null);
							}
						}
					} else if (getController().push(slot.copy()))
					{
						connectedInventory.setInventorySlotContents(currentSlot, null);
					}

					connectedInventory.markDirty();
				}

				currentSlot++;

				if (currentSlot > connectedInventory.getSizeInventory() - 1)
				{
					currentSlot = 0;
				}
			}
		}
	}

	public boolean canImport(ItemStack stack)
	{
		int slots = 0;

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack slot = inventory.getStackInSlot(i);

			if (slot != null)
			{
				slots++;

				if (InventoryUtils.compareStack(stack, slot, compare))
				{
					if (mode == MODE_WHITELIST)
					{
						return true;
					} else if (mode == MODE_BLACKLIST)
					{
						return false;
					}
				}
			}
		}

		if (mode == MODE_WHITELIST)
		{
			return slots == 0;
		}

		return true;
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

		InventoryUtils.restoreInventory(this, nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		nbt.setInteger(NBT_COMPARE, compare);
		nbt.setInteger(NBT_MODE, mode);

		InventoryUtils.saveInventory(this, nbt);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);

		compare = buf.readInt();
		mode = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);

		buf.writeInt(compare);
		buf.writeInt(mode);
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
