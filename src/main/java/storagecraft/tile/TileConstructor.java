package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import storagecraft.inventory.InventorySimple;
import storagecraft.util.InventoryUtils;

public class TileConstructor extends TileMachine implements IInventory, ISidedInventory, ICompareSetting
{
	public static final String NBT_COMPARE = "Compare";

	private InventorySimple inventory = new InventorySimple("constructor", 1);

	private int compare = 0;

	@Override
	public int getEnergyUsage()
	{
		return 1;
	}

	@Override
	public void updateMachine()
	{
		if (ticks % 10 == 0)
		{
			BlockPos front = pos.offset(getDirection());

			if (worldObj.isAirBlock(front) && inventory.getStackInSlot(0) != null)
			{
				ItemStack took = getController().take(inventory.getStackInSlot(0).copy(), compare);

				if (took != null)
				{
					worldObj.setBlockState(front, ((ItemBlock) took.getItem()).getBlock().getDefaultState(), 1 | 2);
				}
			}
		}
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

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		if (nbt.hasKey(NBT_COMPARE))
		{
			compare = nbt.getInteger(NBT_COMPARE);
		}

		InventoryUtils.restoreInventory(this, nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		nbt.setInteger(NBT_COMPARE, compare);

		InventoryUtils.saveInventory(this, nbt);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);

		compare = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);

		buf.writeInt(compare);
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
