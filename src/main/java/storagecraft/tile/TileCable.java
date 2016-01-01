package storagecraft.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import storagecraft.StorageCraftBlocks;
import storagecraft.block.BlockCable;

import java.util.List;

public class TileCable extends TileBase
{
	public static boolean isCable(World world, BlockPos pos)
	{
		return world.getBlockState(pos).getBlock() instanceof BlockCable;
	}

	public boolean hasConnection(EnumFacing dir)
	{
		if (!isCable(worldObj, pos.offset(dir)))
		{
			TileEntity tile = worldObj.getTileEntity(pos.offset(dir));

			return tile instanceof TileMachine || tile instanceof TileController;
		}

		return true;
	}

	public boolean isPowered()
	{
		return worldObj.isBlockPowered(pos);
	}

	public boolean isSensitiveCable()
	{
		if (worldObj.getBlockState(pos).getBlock() == StorageCraftBlocks.CABLE)
		{
			return (Boolean) worldObj.getBlockState(pos).getValue(BlockCable.SENSITIVE);
		}

		return false;
	}

	public boolean isEnabled()
	{
		if (isSensitiveCable())
		{
			return !isPowered();
		}

		return true;
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
