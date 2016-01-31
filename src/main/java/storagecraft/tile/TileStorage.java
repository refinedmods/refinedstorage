package storagecraft.tile;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import storagecraft.block.BlockStorage;
import storagecraft.block.EnumStorageType;
import storagecraft.storage.IStorage;
import storagecraft.storage.IStorageProvider;
import storagecraft.storage.NBTStorage;
import storagecraft.storage.StorageItem;

public class TileStorage extends TileMachine implements IStorageProvider, IStorage
{
	public static final String STORAGE_NBT = "Storage";

	private NBTTagCompound tag = NBTStorage.getBaseNBT();

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

		if (nbt.hasKey(STORAGE_NBT))
		{
			tag = nbt.getCompoundTag(STORAGE_NBT);
		}
	}

	public NBTStorage getStorage()
	{
		return new NBTStorage(tag, ((EnumStorageType) worldObj.getBlockState(pos).getValue(BlockStorage.TYPE)).getCapacity());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		nbt.setTag(STORAGE_NBT, tag);
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
}
