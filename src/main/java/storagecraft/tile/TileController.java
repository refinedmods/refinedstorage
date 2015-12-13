package storagecraft.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import storagecraft.storage.IStorage;
import storagecraft.storage.IStorageProvider;
import storagecraft.storage.StorageItem;

public class TileController extends TileSC implements IEnergyReceiver, INetworkTile {
	public static final int BASE_ENERGY_USAGE = 100;

	private List<StorageItem> items = new ArrayList<StorageItem>();
	private List<IStorage> storages = new ArrayList<IStorage>();
	private List<TileMachine> connectedMachines = new ArrayList<TileMachine>();

	private EnergyStorage energy = new EnergyStorage(32000);
	private int energyUsage;

	private boolean destroyed = false;
	private int ticks = 0;

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (destroyed) {
			return;
		}

		++ticks;

		if (!worldObj.isRemote) {
			if (ticks % 40 == 0) {
				if (!isActive()) {
					disconnectAll();
				} else {
					List<TileMachine> machines = new ArrayList<TileMachine>();

					for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
						TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

						if (tile instanceof TileCable) {
							machines.addAll(((TileCable) tile).findMachines(this));
						}
					}

					for (TileMachine machine : connectedMachines) {
						if (!machines.contains(machine)) {
							machine.onDisconnected();
						}
					}

					for (TileMachine machine : machines) {
						if (!connectedMachines.contains(machine)) {
							machine.onConnected(this);
						}
					}

					connectedMachines = machines;

					storageSync();
				}

				energyUsage = BASE_ENERGY_USAGE;

				for (TileMachine machine : connectedMachines) {
					energyUsage += machine.getEnergyUsage();
				}
			}

			energy.extractEnergy(energyUsage, false);
		} else {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	public void onDestroyed() {
		disconnectAll();

		destroyed = true;
	}

	private void disconnectAll() {
		for (TileMachine machine : connectedMachines) {
			machine.onDisconnected();
		}

		connectedMachines.clear();
	}

	public List<TileMachine> getMachines() {
		return connectedMachines;
	}

	public List<StorageItem> getItems() {
		return items;
	}

	public void storageSync() {
		storages.clear();

		for (TileMachine machine : connectedMachines) {
			if (machine instanceof IStorageProvider) {
				((IStorageProvider) machine).addStorages(storages);
			}
		}

		storageItemsSync();
	}

	private void storageItemsSync() {
		items.clear();

		for (IStorage storage : storages) {
			items.addAll(storage.getAll());
		}
	}

	public boolean push(ItemStack stack) {
		IStorage storageThatCanPush = null;

		for (IStorage storage : storages) {
			if (storage.canPush(stack)) {
				storageThatCanPush = storage;

				break;
			}
		}

		if (storageThatCanPush == null) {
			return false;
		}

		storageThatCanPush.push(stack);

		storageItemsSync();

		return true;
	}

	public ItemStack take(Item type, int quantity, int meta) {
		int took = 0;

		for (IStorage storage : storages) {
			took += storage.take(type, quantity, meta);

			if (took == quantity) {
				break;
			}
		}

		storageItemsSync();

		return new ItemStack(type, took, meta);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		energy.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		energy.writeToNBT(nbt);
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return energy.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return energy.getMaxEnergyStored();
	}

	public int getEnergyUsage() {
		return energyUsage;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	public boolean isActive() {
		return energy.getEnergyStored() >= getEnergyUsage();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		energy.setEnergyStored(buf.readInt());
		energyUsage = buf.readInt();

		items.clear();

		int size = buf.readInt();

		for (int i = 0; i < size; ++i) {
			Item type = Item.getItemById(buf.readInt());
			int quantity = buf.readInt();
			int meta = buf.readInt();

			items.add(new StorageItem(type, quantity, meta));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energy.getEnergyStored());
		buf.writeInt(energyUsage);

		buf.writeInt(items.size());

		for (StorageItem item : items) {
			buf.writeInt(Item.getIdFromItem(item.getType()));
			buf.writeInt(item.getQuantity());
			buf.writeInt(item.getMeta());
		}
	}
}
