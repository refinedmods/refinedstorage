package storagecraft.tile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import storagecraft.util.InventoryUtils;

public class TileDestructor extends TileMachine
{
	@Override
	public int getEnergyUsage()
	{
		return 1;
	}

	@Override
	public void updateMachine()
	{
		if (ticks % 10 == 0)
		{
			BlockPos front = pos.offset(getDirection());

			IBlockState frontBlockState = worldObj.getBlockState(front);
			Block frontBlock = frontBlockState.getBlock();

			if (!frontBlock.isAir(frontBlockState, worldObj, front))
			{
				List<ItemStack> drops = frontBlock.getDrops(worldObj, front, worldObj.getBlockState(front), 0);

				worldObj.setBlockToAir(front);

				for (ItemStack drop : drops)
				{
					if (!getController().push(drop))
					{
						InventoryUtils.dropStack(worldObj, drop, front.getX(), front.getY(), front.getZ());
					}
				}
			}
		}
	}
}
