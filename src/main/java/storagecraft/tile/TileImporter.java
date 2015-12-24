package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
		TileEntity tile = worldObj.getTileEntity(xCoord + getDirection().offsetX, yCoord + getDirection().offsetY, zCoord + getDirection().offsetZ);

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

						if (sided.canExtractItem(currentSlot, slot.copy(), getDirection().getOpposite().ordinal()))
						{
							if (getController().push(slot.copy()))
							{
								connectedInventory.setInventorySlotContents(currentSlot, null);
							}
						}
					}
					else if (getController().push(slot.copy()))
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
					}
					else if (mode == MODE_BLACKLIST)
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
		this.compare = compare;
	}

	public int getMode()
	{
		return mode;
	}

	public void setMode(int mode)
	{
		this.mode = mode;
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
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return new int[]
		{
		};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side)
	{
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side)
	{
		return false;
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
}
