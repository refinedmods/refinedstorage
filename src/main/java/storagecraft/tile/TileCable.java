package storagecraft.tile;

import java.util.List;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import storagecraft.block.BlockCable;

public class TileCable extends TileBase
{
	public static boolean isCable(IBlockAccess world, BlockPos pos)
	{
		return world.getBlockState(pos).getBlock() instanceof BlockCable;
	}

	public boolean hasConnection(EnumFacing dir)
	{
		if (!isEnabled())
		{
			return false;
		}

		if (isCable(worldObj, pos.offset(dir)))
		{
			return true;
		}

		TileEntity tile = worldObj.getTileEntity(pos.offset(dir));

		return tile instanceof TileMachine || tile instanceof TileController;
	}

	public boolean isEnabled()
	{
		return !worldObj.isBlockPowered(pos);
	}

	public void addMachines(List<BlockPos> visited, List<TileMachine> machines, TileController controller)
	{
		for (BlockPos visitedBlock : visited)
		{
			if (visitedBlock.equals(pos))
			{
				return;
			}
		}

		visited.add(pos);

		for (EnumFacing dir : EnumFacing.VALUES)
		{
			BlockPos newPos = pos.offset(dir);

			boolean found = false;

			for (BlockPos visitedBlock : visited)
			{
				if (visitedBlock.equals(newPos))
				{
					found = true;
				}
			}

			if (found)
			{
				continue;
			}

			TileEntity tile = worldObj.getTileEntity(newPos);

			if (tile instanceof TileMachine && ((TileMachine) tile).getRedstoneMode().isEnabled(worldObj, newPos))
			{
				machines.add((TileMachine) tile);

				visited.add(newPos);
			}
			else if (tile instanceof TileCable && ((TileCable) tile).isEnabled())
			{
				((TileCable) tile).addMachines(visited, machines, controller);
			}
			else if (tile instanceof TileController && !controller.getPos().equals(newPos))
			{
				worldObj.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.5f, true);
			}
		}
	}
}
