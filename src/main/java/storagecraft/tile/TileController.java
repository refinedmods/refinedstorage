package storagecraft.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import storagecraft.StorageCraftBlocks;
import storagecraft.block.BlockController;
import storagecraft.block.EnumControllerType;
import storagecraft.storage.IStorage;
import storagecraft.storage.IStorageProvider;
import storagecraft.storage.StorageItem;
import storagecraft.tile.settings.IRedstoneModeSetting;
import storagecraft.tile.settings.RedstoneMode;
import storagecraft.util.InventoryUtils;

public class TileController extends TileBase implements IEnergyReceiver, INetworkTile, IRedstoneModeSetting
{
	private List<StorageItem> items = new ArrayList<StorageItem>();
	private List<IStorage> storages = new ArrayList<IStorage>();

	private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

	private List<TileMachine> machines = new ArrayList<TileMachine>();

	private List<BlockPos> visitedCables = new ArrayList<BlockPos>();

	private EnergyStorage energy = new EnergyStorage(32000);
	private int energyUsage;

	private boolean destroyed = false;

	@Override
	public void update()
	{
		super.update();

		if (destroyed)
		{
			return;
		}

		if (!worldObj.isRemote)
		{
			int lastEnergy = energy.getEnergyStored();

			if (ticks % 40 == 0)
			{
				if (!isActive())
				{
					disconnectAll();
				}
				else
				{
					visitedCables.clear();

					List<TileMachine> newMachines = new ArrayList<TileMachine>();

					for (EnumFacing dir : EnumFacing.VALUES)
					{
						TileEntity tile = worldObj.getTileEntity(pos.offset(dir));

						if (tile instanceof TileCable)
						{
							TileCable cable = (TileCable) tile;

							if (cable.isEnabled())
							{
								cable.addMachines(visitedCables, newMachines, this);
							}
						}
					}

					for (TileMachine machine : machines)
					{
						if (!newMachines.contains(machine))
						{
							machine.onDisconnected();
						}
					}

					for (TileMachine machine : newMachines)
					{
						if (!machines.contains(machine))
						{
							machine.onConnected(this);
						}
					}

					machines = newMachines;

					storages.clear();

					for (TileMachine machine : machines)
					{
						if (machine instanceof IStorageProvider)
						{
							((IStorageProvider) machine).addStorages(storages);
						}
					}

					storages.sort(new Comparator<IStorage>()
					{
						@Override
						public int compare(IStorage s1, IStorage s2)
						{
							return (s1.getPriority() > s2.getPriority()) ? -1 : 1;
						}
					});

					syncItems();
				}

				energyUsage = 10;

				for (TileMachine machine : machines)
				{
					energyUsage += machine.getEnergyUsage();
				}
			}

			switch (getType())
			{
				case NORMAL:
					energy.extractEnergy(energyUsage, false);
					break;
				case CREATIVE:
					energy.setEnergyStored(energy.getMaxEnergyStored());
					break;
			}

			if (lastEnergy != energy.getEnergyStored())
			{
				worldObj.updateComparatorOutputLevel(pos, StorageCraftBlocks.CONTROLLER);
			}
		}
	}

	public EnumControllerType getType()
	{
		if (worldObj.getBlockState(pos).getBlock() == StorageCraftBlocks.CONTROLLER)
		{
			return (EnumControllerType) worldObj.getBlockState(pos).getValue(BlockController.TYPE);
		}

		return EnumControllerType.NORMAL;
	}

	public void onDestroyed()
	{
		disconnectAll();

		destroyed = true;
	}

	private void disconnectAll()
	{
		for (TileMachine machine : machines)
		{
			machine.onDisconnected();
		}

		machines.clear();
	}

	public List<TileMachine> getMachines()
	{
		return machines;
	}

	public List<StorageItem> getItems()
	{
		return items;
	}

	private void syncItems()
	{
		items.clear();

		for (IStorage storage : storages)
		{
			storage.addItems(items);
		}

		combineItems();
	}

	private void combineItems()
	{
		List<Integer> markedIndexes = new ArrayList<Integer>();

		for (int i = 0; i < items.size(); ++i)
		{
			if (markedIndexes.contains(i))
			{
				continue;
			}

			StorageItem item = items.get(i);

			for (int j = i + 1; j < items.size(); ++j)
			{
				if (markedIndexes.contains(j))
				{
					continue;
				}

				StorageItem other = items.get(j);

				if (item.compareNoQuantity(other))
				{
					item.setQuantity(item.getQuantity() + other.getQuantity());

					markedIndexes.add(j);
				}
			}
		}

		List<StorageItem> markedItems = new ArrayList<StorageItem>();

		for (int i : markedIndexes)
		{
			markedItems.add(items.get(i));
		}

		items.removeAll(markedItems);
	}

	public boolean push(ItemStack stack)
	{
		IStorage foundStorage = null;

		for (IStorage storage : storages)
		{
			if (storage.canPush(stack))
			{
				foundStorage = storage;

				break;
			}
		}

		if (foundStorage == null)
		{
			return false;
		}

		foundStorage.push(stack);

		syncItems();

		markDirty();

		return true;
	}

	public ItemStack take(ItemStack stack)
	{
		markDirty();

		return take(stack, InventoryUtils.COMPARE_DAMAGE | InventoryUtils.COMPARE_NBT);
	}

	public ItemStack take(ItemStack stack, int flags)
	{
		markDirty();

		int requested = stack.stackSize;
		int receiving = 0;

		ItemStack newStack = null;

		for (IStorage storage : storages)
		{
			ItemStack took = storage.take(stack, flags);

			if (took != null)
			{
				if (newStack == null)
				{
					newStack = took;
				}
				else
				{
					newStack.stackSize += took.stackSize;
				}

				receiving += took.stackSize;
			}

			if (requested == receiving)
			{
				break;
			}
		}

		syncItems();

		return newStack;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		energy.readFromNBT(nbt);

		if (nbt.hasKey(RedstoneMode.NBT))
		{
			redstoneMode = RedstoneMode.getById(nbt.getInteger(RedstoneMode.NBT));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);

		energy.writeToNBT(nbt);

		nbt.setInteger(RedstoneMode.NBT, redstoneMode.id);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate)
	{
		return energy.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from)
	{
		return energy.getEnergyStored();
	}

	public int getEnergyScaled(int i)
	{
		return (int) ((float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored() * (float) i);
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{
		return energy.getMaxEnergyStored();
	}

	public int getEnergyUsage()
	{
		return energyUsage;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from)
	{
		return true;
	}

	public boolean isActive()
	{
		return energy.getEnergyStored() >= getEnergyUsage() && redstoneMode.isEnabled(worldObj, pos);
	}

	@Override
	public RedstoneMode getRedstoneMode()
	{
		return redstoneMode;
	}

	@Override
	public void setRedstoneMode(RedstoneMode mode)
	{
		markDirty();

		this.redstoneMode = mode;
	}

	@Override
	public BlockPos getTilePos()
	{
		return pos;
	}

	@Override
	public BlockPos getMachinePos()
	{
		return pos;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int lastEnergy = energy.getEnergyStored();

		energy.setEnergyStored(buf.readInt());

		if (lastEnergy != energy.getEnergyStored())
		{
			worldObj.markBlockForUpdate(pos);
		}

		energyUsage = buf.readInt();

		redstoneMode = RedstoneMode.getById(buf.readInt());

		items.clear();

		int size = buf.readInt();

		for (int i = 0; i < size; ++i)
		{
			items.add(new StorageItem(buf));
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(energy.getEnergyStored());
		buf.writeInt(energyUsage);

		buf.writeInt(redstoneMode.id);

		buf.writeInt(items.size());

		for (StorageItem item : items)
		{
			item.toBytes(buf, items.indexOf(item));
		}
	}
}
