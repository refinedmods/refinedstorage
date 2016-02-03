package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import storagecraft.StorageCraft;
import storagecraft.block.BlockStorage;
import storagecraft.block.EnumStorageType;
import storagecraft.inventory.InventorySimple;
import storagecraft.network.MessagePriorityUpdate;
import storagecraft.storage.IStorage;
import storagecraft.storage.IStorageGui;
import storagecraft.storage.IStorageProvider;
import storagecraft.storage.NBTStorage;
import storagecraft.storage.StorageItem;
import storagecraft.tile.settings.ICompareSetting;
import storagecraft.tile.settings.IModeSetting;
import storagecraft.tile.settings.IRedstoneModeSetting;
import storagecraft.util.InventoryUtils;

public class TileStorage extends TileMachine implements IStorageProvider, IStorage, IStorageGui, ICompareSetting, IModeSetting
{
	public static final String NBT_STORAGE = "Storage";
	public static final String NBT_PRIORITY = "Priority";
	public static final String NBT_COMPARE = "Compare";
	public static final String NBT_MODE = "Mode";

	private InventorySimple inventory = new InventorySimple("storage", 9, this);

	private NBTTagCompound tag = NBTStorage.getBaseNBT();

	private int priority = 0;
	private int compare = 0;
	private int mode = 0;

	@SideOnly(Side.CLIENT)
	private int stored;

	@Override
	public int getEnergyUsage()
	{
		return 1;
	}

	@Override
	public void updateMachine()
	{
	}

	@Override
	public void addStorages(List<IStorage> storages)
	{
		storages.add(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		InventoryUtils.restoreInventory(inventory, 0, nbt);

		if (nbt.hasKey(NBT_STORAGE))
		{
			tag = nbt.getCompoundTag(NBT_STORAGE);
		}

		if (nbt.hasKey(NBT_PRIORITY))
		{
			priority = nbt.getInteger(NBT_PRIORITY);
		}

		if (nbt.hasKey(NBT_COMPARE))
		{
			compare = nbt.getInteger(NBT_COMPARE);
		}

		if (nbt.hasKey(NBT_MODE))
		{
			mode = nbt.getInteger(NBT_MODE);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		InventoryUtils.saveInventory(inventory, 0, nbt);

		nbt.setTag(NBT_STORAGE, tag);
		nbt.setInteger(NBT_PRIORITY, priority);
		nbt.setInteger(NBT_COMPARE, compare);
		nbt.setInteger(NBT_MODE, mode);
	}

	public EnumStorageType getType()
	{
		return ((EnumStorageType) worldObj.getBlockState(pos).getValue(BlockStorage.TYPE));
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);

		buf.writeInt(NBTStorage.getStored(tag));
		buf.writeInt(priority);
		buf.writeInt(compare);
		buf.writeInt(mode);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);

		stored = buf.readInt();
		priority = buf.readInt();
		compare = buf.readInt();
		mode = buf.readInt();
	}

	@Override
	public void addItems(List<StorageItem> items)
	{
		getStorage().addItems(items);

		markDirty();
	}

	@Override
	public void push(ItemStack stack)
	{
		getStorage().push(stack);

		markDirty();
	}

	@Override
	public ItemStack take(ItemStack stack, int flags)
	{
		ItemStack result = getStorage().take(stack, flags);

		markDirty();

		return result;
	}

	@Override
	public boolean canPush(ItemStack stack)
	{
		return checkWhitelistBlacklist(inventory, this, compare, stack) && getStorage().canPush(stack);
	}

	public static boolean checkWhitelistBlacklist(IInventory inventory, IModeSetting mode, int compare, ItemStack stack)
	{
		if (mode.isWhitelist())
		{
			int slots = 0;

			for (int i = 0; i < inventory.getSizeInventory(); ++i)
			{
				ItemStack slot = inventory.getStackInSlot(i);

				if (slot != null)
				{
					slots++;

					if (InventoryUtils.compareStack(slot, stack, compare))
					{
						return true;
					}
				}
			}

			return slots == 0;
		}
		else if (mode.isBlacklist())
		{
			for (int i = 0; i < inventory.getSizeInventory(); ++i)
			{
				ItemStack slot = inventory.getStackInSlot(i);

				if (slot != null && InventoryUtils.compareStack(slot, stack, compare))
				{
					return false;
				}
			}

			return true;
		}

		return false;
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

	@Override
	public boolean isWhitelist()
	{
		return mode == 0;
	}

	@Override
	public boolean isBlacklist()
	{
		return mode == 1;
	}

	@Override
	public void setToWhitelist()
	{
		markDirty();

		this.mode = 0;
	}

	@Override
	public void setToBlacklist()
	{
		markDirty();

		this.mode = 1;
	}

	@Override
	public String getName()
	{
		return "block.storagecraft:storage." + getType().getId() + ".name";
	}

	@Override
	public IInventory getInventory()
	{
		return inventory;
	}

	@Override
	public IRedstoneModeSetting getRedstoneModeSetting()
	{
		return this;
	}

	@Override
	public ICompareSetting getCompareSetting()
	{
		return this;
	}

	@Override
	public IModeSetting getWhitelistBlacklistSetting()
	{
		return this;
	}

	@Override
	public IPriorityHandler getPriorityHandler()
	{
		return new IPriorityHandler()
		{
			@Override
			public void onPriorityChanged(int priority)
			{
				StorageCraft.NETWORK.sendToServer(new MessagePriorityUpdate(pos, priority));
			}
		};
	}

	@Override
	public NBTStorage getStorage()
	{
		return new NBTStorage(tag, getCapacity(), priority);
	}

	public NBTTagCompound getStorageTag()
	{
		return tag;
	}

	public void setStorageTag(NBTTagCompound tag)
	{
		markDirty();

		this.tag = tag;
	}

	@Override
	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		markDirty();

		this.priority = priority;
	}

	@Override
	public int getStored()
	{
		return stored;
	}

	public int getStoredScaled(int scale)
	{
		if (getType() == EnumStorageType.TYPE_CREATIVE)
		{
			return 0;
		}

		return (int) ((float) getStored() / (float) getCapacity() * (float) scale);
	}

	@Override
	public int getCapacity()
	{
		return getType().getCapacity();
	}
}
