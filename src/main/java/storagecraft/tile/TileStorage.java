package storagecraft.tile;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import storagecraft.block.BlockStorage;
import storagecraft.block.EnumStorageType;
import storagecraft.inventory.InventorySimple;
import storagecraft.storage.IStorage;
import storagecraft.storage.IStorageProvider;
import storagecraft.storage.NBTStorage;
import storagecraft.storage.StorageItem;
import storagecraft.util.InventoryUtils;

public class TileStorage extends TileMachine implements IStorageProvider, IStorage, IInventory, ISidedInventory
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

		InventoryUtils.restoreInventory(this, nbt);

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

	public NBTStorage getStorage()
	{
		return new NBTStorage(tag, getType().getCapacity(), priority);
	}

	public EnumStorageType getType()
	{
		return ((EnumStorageType) worldObj.getBlockState(pos).getValue(BlockStorage.TYPE));
	}

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

		return (int) ((float) getStored() / (float) getType().getCapacity() * (float) scale);
	}

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
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
