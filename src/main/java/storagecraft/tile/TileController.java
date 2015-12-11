package storagecraft.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import storagecraft.storage.Storage;

public class TileController extends TileSC implements IEnergyHandler, INetworkTile {
	public static final int BASE_ENERGY_USAGE = 100;

	private Storage storage = new Storage();

	private boolean destroyed = false;

	private EnergyStorage energy = new EnergyStorage(32000);
	private int energyUsage;

	private List<TileMachine> connectedMachines = new ArrayList<TileMachine>();

	private int ticks = 0;

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (!destroyed) {
			++ticks;

			if (!worldObj.isRemote && ticks % 40 == 0) {
				storage.push(new ItemStack(worldObj.rand.nextBoolean() ? Items.diamond : Items.apple, 10 + worldObj.rand.nextInt(40)));

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
				}

				energyUsage = BASE_ENERGY_USAGE;

				for (TileMachine machine : connectedMachines) {
					energyUsage += machine.getEnergyUsage();
				}

				energy.extractEnergy(energyUsage, false);
			} else {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
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

	public Storage getStorage() {
		return storage;
	}

	public List<TileMachine> getMachines() {
		return connectedMachines;
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
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return energy.extractEnergy(maxExtract, simulate);
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

		storage.fromBytes(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energy.getEnergyStored());
		buf.writeInt(energyUsage);

		storage.toBytes(buf);
	}
}
