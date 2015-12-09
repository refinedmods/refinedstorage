package storagecraft.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileController extends TileSC implements IEnergyHandler, INetworkTile {
	public static final int BASE_ENERGY_USAGE = 100;

	private int energyUsage;
	private EnergyStorage storage = new EnergyStorage(32000);
	private List<IMachine> connectedMachines = new ArrayList<IMachine>();
	private int ticks = 0;

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (!worldObj.isRemote) {
			ticks++;

			if (ticks % 40 == 0) {
				if (!isActive()) {
					disconnectAll();
				} else {
					List<IMachine> machines = new ArrayList<IMachine>();

					for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
						TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

						if (tile instanceof TileCable) {
							machines.addAll(((TileCable) tile).findMachines(this));
						}
					}

					for (IMachine machine : connectedMachines) {
						if (!machines.contains(machine)) {
							machine.onDisconnected();
						}
					}

					for (IMachine machine : machines) {
						if (!connectedMachines.contains(machine)) {
							machine.onConnected(this);
						}
					}

					connectedMachines = machines;
				}
			}

			energyUsage = BASE_ENERGY_USAGE;

			for (IMachine machine : connectedMachines) {
				energyUsage += machine.getEnergyUsage();
			}
		}

		storage.extractEnergy(getEnergyUsage(), false);
	}

	public void onDestroyed() {
		disconnectAll();
	}

	private void disconnectAll() {
		for (IMachine machine : connectedMachines) {
			machine.onDisconnected();
		}

		connectedMachines.clear();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		storage.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		storage.writeToNBT(nbt);
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return storage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return storage.getMaxEnergyStored();
	}

	public int getEnergyUsage() {
		return energyUsage;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	public boolean isActive() {
		return storage.getEnergyStored() >= getEnergyUsage();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		storage.setEnergyStored(buf.readInt());
		energyUsage = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(storage.getEnergyStored());
		buf.writeInt(energyUsage);
	}
}
