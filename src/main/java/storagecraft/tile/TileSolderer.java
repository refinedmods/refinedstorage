package storagecraft.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import storagecraft.inventory.InventorySimple;
import storagecraft.tile.solderer.ISoldererRecipe;
import storagecraft.tile.solderer.SoldererRegistry;
import storagecraft.util.InventoryUtils;

// @TODO: Write working and progress to NBT
public class TileSolderer extends TileMachine implements IInventory
{
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
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		InventoryUtils.restoreInventory(this, nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		InventoryUtils.saveInventory(this, nbt);
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
