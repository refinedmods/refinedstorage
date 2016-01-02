package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import storagecraft.inventory.InventorySimple;
import storagecraft.tile.solderer.ISoldererRecipe;
import storagecraft.tile.solderer.SoldererRegistry;
import storagecraft.util.InventoryUtils;

public class TileSolderer extends TileMachine implements IInventory, ISidedInventory
{
	public static final String NBT_WORKING = "Working";
	public static final String NBT_PROGRESS = "Progress";

	private InventorySimple inventory = new InventorySimple("solderer", 4);
	private ISoldererRecipe recipe;
	private boolean working = false;
	private int progress = 0;
	@SideOnly(Side.CLIENT)
	private int duration;

	@Override
	public int getEnergyUsage()
	{
		return 3;
	}

	@Override
	public void updateMachine()
	{
		ISoldererRecipe newRecipe = SoldererRegistry.getRecipe(inventory);

		if (newRecipe == null)
		{
			reset();
		}
		else if (newRecipe != recipe && inventory.getStackInSlot(3) == null)
		{
			recipe = newRecipe;
			progress = 0;
			working = true;

			markDirty();
		}
		else if (working)
		{
			progress++;

			if (progress == recipe.getDuration())
			{
				inventory.setInventorySlotContents(3, recipe.getResult());

				for (int i = 0; i < 3; ++i)
				{
					if (recipe.getRow(i) != null)
					{
						inventory.decrStackSize(i, recipe.getRow(i).stackSize);
					}
				}

				reset();
			}
		}
	}

	@Override
	public void onDisconnected()
	{
		super.onDisconnected();

		reset();
	}

	public void reset()
	{
		progress = 0;
		working = false;
		recipe = null;

		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		InventoryUtils.restoreInventory(this, nbt);

		recipe = SoldererRegistry.getRecipe(inventory);

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
		duration = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);

		buf.writeBoolean(working);
		buf.writeInt(progress);
		buf.writeInt(recipe != null ? recipe.getDuration() : 0);
	}

	public boolean isWorking()
	{
		return working;
	}

	public int getProgress()
	{
		return progress;
	}

	@SideOnly(Side.CLIENT)
	public int getProgressScaled(int i)
	{
		return (int) ((float) progress / (float) duration * (float) i);
	}

	@SideOnly(Side.CLIENT)
	public int getDuration()
	{
		return duration;
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
		// On all sides, but not the bottom we can reach the slots
		if (side != EnumFacing.DOWN)
		{
			return new int[]
				{
					0, 1, 2
				};
		}

		// On the bottom we can only reach the output slot
		return new int[]
			{
				3
			};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing direction)
	{
		return slot != 3;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction)
	{
		return slot == 3;
	}
}
