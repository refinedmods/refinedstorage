package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import storagecraft.SC;
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
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			player.openGui(SC.INSTANCE, SC.GUI.CONTROLLER, world, x, y, z);
		}

		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		((TileController) world.getTileEntity(x, y, z)).onDestroyed();

		super.onBlockPreDestroy(world, x, y, z, meta);
	}
}
