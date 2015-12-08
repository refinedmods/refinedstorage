package storagecraft.tile;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileController extends TileSC {
	private List<IMachine> connectedMachines = new ArrayList<IMachine>();

	private int ticks = 0;

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (!worldObj.isRemote) {
			ticks++;

			if (ticks % 40 == 0) {
				List<IMachine> machines = new ArrayList<IMachine>();

				for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
					TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

					if (tile instanceof TileCable) {
						machines.addAll(((TileCable) tile).findMachines());
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
	}

	public void onDestroyed() {
		for (IMachine machine : connectedMachines) {
			machine.onDisconnected();
		}

		connectedMachines.clear();
	}
}
