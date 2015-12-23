package storagecraft.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
	private int progress;
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
		// On all sides, but not the bottom we can reach the slots
		if (side > 0)
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
	public boolean canInsertItem(int slot, ItemStack stack, int side)
	{
		// We can insert in all slots, but not the output slot or via the output side
		return side != 0 && slot != 3;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side)
	{
		// We can only extract from the buttom in the last slot
		return side == 0 && slot == 3;
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
}
