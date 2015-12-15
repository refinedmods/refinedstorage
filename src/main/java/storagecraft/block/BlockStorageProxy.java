package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import storagecraft.tile.TileStorageProxy;

public class BlockStorageProxy extends BlockSC implements ITileEntityProvider {
	public BlockStorageProxy() {
		super("storageProxy");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileStorageProxy();
	}
}
