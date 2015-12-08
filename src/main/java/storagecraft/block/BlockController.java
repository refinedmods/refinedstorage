package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import storagecraft.tile.TileController;

public class BlockController extends BlockSC implements ITileEntityProvider {
	public BlockController() {
		super("controller");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileController();
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		((TileController) world.getTileEntity(x, y, z)).onDestroyed();

		super.onBlockPreDestroy(world, x, y, z, meta);
	}
}
