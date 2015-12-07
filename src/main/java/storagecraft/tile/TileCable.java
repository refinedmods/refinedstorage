package storagecraft.tile;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import storagecraft.block.BlockCable;

public class TileCable extends TileSC {
	public boolean hasConnection(ForgeDirection dir) {
		Block block = worldObj.getBlock(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

		if (!(block instanceof BlockCable)) {
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

			return tile instanceof IMachine;
		}

		return true;
	}
}
