package storagecraft.tile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
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

			Block frontBlock = worldObj.getBlockState(front).getBlock();

			if (!frontBlock.isAir(worldObj, front))
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
