package storagecraft.tile;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import storagecraft.block.BlockCable;

public class TileCable extends TileSC {
	public static boolean isCable(World world, int x, int y, int z, ForgeDirection dir) {
		Block block = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);

		return block instanceof BlockCable;
	}

	public boolean hasConnection(ForgeDirection dir) {
		if (!isCable(worldObj, xCoord, yCoord, zCoord, dir)) {
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

			return tile instanceof IMachine || tile instanceof TileController;
		}

		return true;
	}

	public List<IMachine> findMachines() {
		return findMachines(new ArrayList(), true);
	}

	private List<IMachine> findMachines(List<Vec3> visited, boolean ignoreController) {
		List<IMachine> machines = new ArrayList<IMachine>();

		for (Vec3 visitedCable : visited) {
			if (visitedCable.xCoord == xCoord && visitedCable.yCoord == yCoord && visitedCable.zCoord == zCoord) {
				return machines;
			}
		}

		visited.add(Vec3.createVectorHelper(xCoord, yCoord, zCoord));

		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

			if (tile instanceof IMachine) {
				machines.add((IMachine) tile);
			} else if (tile instanceof TileCable) {
				machines.addAll(((TileCable) tile).findMachines(visited, false));
			} else if (tile instanceof TileController && !ignoreController) {
				worldObj.createExplosion(null, xCoord, yCoord, zCoord, 4.5f, true);
			}
		}

		return machines;
	}
}
