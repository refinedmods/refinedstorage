package storagecraft.tile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
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
		int frontX = xCoord + getDirection().offsetX;
		int frontY = yCoord + getDirection().offsetY;
		int frontZ = zCoord + getDirection().offsetZ;

		Block front = worldObj.getBlock(frontX, frontY, frontZ);

		if (front != Blocks.air)
		{
			List<ItemStack> drops = front.getDrops(worldObj, frontX, frontY, frontZ, worldObj.getBlockMetadata(frontX, frontY, frontZ), 0);

			worldObj.setBlockToAir(frontX, frontY, frontZ);

			for (ItemStack drop : drops)
			{
				if (!getController().push(drop))
				{
					InventoryUtils.dropStack(worldObj, drop, frontX, frontY, frontZ);
				}
			}
		}
	}
}
