package storagecraft.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import storagecraft.storage.IStorage;
import storagecraft.storage.IStorageProvider;
import storagecraft.storage.StorageItem;
import storagecraft.util.InventoryUtils;

public class TileController extends TileBase implements IEnergyReceiver, INetworkTile {
	private List<StorageItem> items = new ArrayList<StorageItem>();
	private List<IStorage> storages = new ArrayList<IStorage>();

	private List<TileMachine> machines = new ArrayList<TileMachine>();

	private List<Vec3> visitedCables = new ArrayList<Vec3>();

	private EnergyStorage energy = new EnergyStorage(32000);
	private int energyUsage;

	private boolean destroyed = false;

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (destroyed) {
			return;
		}

		if (!worldObj.isRemote) {
			if (ticks % 40 == 0) {
				if (!isActive()) {
					disconnectAll();
				} else {
					visitedCables.clear();

					List<TileMachine> newMachines = new ArrayList<TileMachine>();

					for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
						TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

						if (tile instanceof TileCable) {
							((TileCable) tile).addMachines(visitedCables, newMachines, this);
						}
					}

					for (TileMachine machine : machines) {
						if (!newMachines.contains(machine)) {
							machine.onDisconnected();
						}
					}

					for (TileMachine machine : newMachines) {
						if (!machines.contains(machine)) {
							machine.onConnected(this);
						}
					}

					machines = newMachines;

					storages.clear();

					for (TileMachine machine : machines) {
						if (machine instanceof IStorageProvider) {
							((IStorageProvider) machine).addStorages(storages);
						}
					}

					syncItems();
				}

				energyUsage = 10;

				for (TileMachine machine : machines) {
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
		for (TileMachine machine : machines) {
			machine.onDisconnected();
		}

		machines.clear();
	}

	public List<TileMachine> getMachines() {
		return machines;
	}

	public List<StorageItem> getItems() {
		return items;
	}

	private void syncItems() {
		items.clear();

		for (IStorage storage : storages) {
			storage.addItems(items);
		}

		combineItems();
	}

	private void combineItems() {
		List<Integer> markedIndexes = new ArrayList<Integer>();

		for (int i = 0; i < items.size(); ++i) {
			if (markedIndexes.contains(i)) {
				continue;
			}

			StorageItem item = items.get(i);

			for (int j = i + 1; j < items.size(); ++j) {
				if (markedIndexes.contains(j)) {
					continue;
				}

				StorageItem other = items.get(j);

				if (item.compareNoQuantity(other)) {
					item.setQuantity(item.getQuantity() + other.getQuantity());

					markedIndexes.add(j);
				}
			}
		}

		List<StorageItem> markedItems = new ArrayList<StorageItem>();

		for (int i : markedIndexes) {
			markedItems.add(items.get(i));
		}

		items.removeAll(markedItems);
	}

	public boolean push(ItemStack stack) {
		IStorage foundStorage = null;

		for (IStorage storage : storages) {
			if (storage.canPush(stack)) {
				foundStorage = storage;

				break;
			}
		}

		if (foundStorage == null) {
			return false;
		}

		foundStorage.push(stack);

		syncItems();

		return true;
	}

	public ItemStack take(ItemStack stack) {
		return take(stack, InventoryUtils.COMPARE_DAMAGE | InventoryUtils.COMPARE_NBT);
	}

	public ItemStack take(ItemStack stack, int flags) {
		int requested = stack.stackSize;
		int receiving = 0;

		ItemStack newStack = null;

		for (IStorage storage : storages) {
			ItemStack took = storage.take(stack, flags);

			if (took != null) {
				if (newStack == null) {
					newStack = took;
				} else {
					newStack.stackSize += took.stackSize;
				}

				receiving += took.stackSize;
			}

			if (requested == receiving) {
				break;
			}
		}

		syncItems();

		return newStack;
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
			int damage = buf.readInt();
			NBTTagCompound tag = buf.readBoolean() ? ByteBufUtils.readTag(buf) : null;

			items.add(new StorageItem(type, quantity, damage, tag));
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
			buf.writeInt(item.getDamage());
			buf.writeBoolean(item.getTag() != null);

			if (item.getTag() != null) {
				ByteBufUtils.writeTag(buf, item.getTag());
			}
		}
	}
}
