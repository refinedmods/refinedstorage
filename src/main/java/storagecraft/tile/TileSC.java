package storagecraft.tile;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.tileentity.TileEntity;
import storagecraft.SC;
import storagecraft.network.MessageTileUpdate;

public class TileSC extends TileEntity {
	public static final int UPDATE_RANGE = 64;

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (!worldObj.isRemote) {
			if (this instanceof INetworkTile) {
				TargetPoint target = new TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, UPDATE_RANGE);

				SC.NETWORK.sendToAllAround(new MessageTileUpdate(this), target);
			}
		}
	}
}
