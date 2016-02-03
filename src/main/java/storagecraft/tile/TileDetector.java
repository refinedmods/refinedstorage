package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import storagecraft.StorageCraftBlocks;
import storagecraft.inventory.InventorySimple;
import storagecraft.storage.StorageItem;
import storagecraft.tile.settings.ICompareSetting;
import storagecraft.util.InventoryUtils;

public class TileDetector extends TileMachine implements ICompareSetting
{
	public static final int MODE_UNDER = 0;
	public static final int MODE_EQUAL = 1;
	public static final int MODE_ABOVE = 2;

	public static final String NBT_COMPARE = "Compare";
	public static final String NBT_MODE = "Mode";
	public static final String NBT_AMOUNT = "Amount";

	private InventorySimple inventory = new InventorySimple("detector", 1, this);

	private int compare = 0;
	private int mode = MODE_EQUAL;
	private int amount = 0;

	private boolean powered = false;

	public TileDetector()
	{
		this.redstoneControlled = false;
	}

	@Override
	public void onDisconnected()
	{
		super.onDisconnected();

		powered = false;
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
		markDirty();

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

		InventoryUtils.restoreInventory(inventory, 0, nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		nbt.setInteger(NBT_COMPARE, compare);
		nbt.setInteger(NBT_MODE, mode);
		nbt.setInteger(NBT_AMOUNT, amount);

		InventoryUtils.saveInventory(inventory, 0, nbt);
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

	public IInventory getInventory()
	{
		return inventory;
	}
}
