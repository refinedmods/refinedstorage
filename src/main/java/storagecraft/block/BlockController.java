package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.tile.TileController;

public class BlockController extends BlockBase implements ITileEntityProvider {
	private IIcon sideIcon;
	private IIcon[] icons = new IIcon[6];

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
			player.openGui(StorageCraft.INSTANCE, StorageCraft.GUI.CONTROLLER, world, x, y, z);
		}

		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		((TileController) world.getTileEntity(x, y, z)).onDestroyed();

		super.onBlockPreDestroy(world, x, y, z, meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		for (int i = 0; i <= 5; ++i) {
			icons[i] = register.registerIcon("storagecraft:controller" + i);
		}

		sideIcon = register.registerIcon("storagecraft:generic");
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		if (side == 0 || side == 1) {
			return sideIcon;
		}

		TileController controller = (TileController) world.getTileEntity(x, y, z);

		return icons[(int) ((float) controller.getEnergyStored(null) / (float) controller.getMaxEnergyStored(null) * 5f)];
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (side == 0 || side == 1) {
			return sideIcon;
		}

		return icons[0];
	}
}
