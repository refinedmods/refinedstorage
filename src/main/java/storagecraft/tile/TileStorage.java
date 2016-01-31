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
import storagecraft.network.MessageStoragePriorityUpdate;
import storagecraft.storage.IStorage;
import storagecraft.storage.IStorageGui;
import storagecraft.storage.IStorageProvider;
import storagecraft.storage.NBTStorage;
import storagecraft.storage.StorageItem;
import storagecraft.util.InventoryUtils;

public class TileStorage extends TileMachine implements IStorageProvider, IStorage, IStorageGui
{
	public static final String NBT_STORAGE = "Storage";
	public static final String NBT_PRIORITY = "Priority";

	private InventorySimple inventory = new InventorySimple("storage", 9);

	private NBTTagCompound tag = NBTStorage.getBaseNBT();

	private int priority = 0;

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

		InventoryUtils.restoreInventory(inventory, nbt);

		if (nbt.hasKey(NBT_STORAGE))
		{
			tag = nbt.getCompoundTag(NBT_STORAGE);
		}

		if (nbt.hasKey(NBT_PRIORITY))
		{
			priority = nbt.getInteger(NBT_PRIORITY);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		InventoryUtils.saveInventory(inventory, nbt);

		nbt.setTag(NBT_STORAGE, tag);
		nbt.setInteger(NBT_PRIORITY, priority);
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
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);

		stored = buf.readInt();
		priority = buf.readInt();
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
		return getStorage().canPush(stack);
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
	public IPriorityHandler getPriorityHandler()
	{
		return new IPriorityHandler()
		{
			@Override
			public void onPriorityChanged(int priority)
			{
				StorageCraft.NETWORK.sendToServer(new MessageStoragePriorityUpdate(pos, priority));
			}
		};
	}

	@Override
	public NBTStorage getStorage()
	{
		return new NBTStorage(tag, getCapacity(), priority);
	}

	@Override
	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
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
