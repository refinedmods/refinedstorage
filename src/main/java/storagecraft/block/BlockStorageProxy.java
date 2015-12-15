package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.tile.TileStorageProxy;

public class BlockStorageProxy extends BlockSC implements ITileEntityProvider {
	private IIcon frontIcon;
	private IIcon sideIcon;

	public BlockStorageProxy() {
		super("storageProxy");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileStorageProxy();
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		frontIcon = register.registerIcon("storagecraft:storageProxy");
		sideIcon = register.registerIcon("storagecraft:generic");
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileStorageProxy tile = (TileStorageProxy) world.getTileEntity(x, y, z);

		if (side == tile.getDirection().getOpposite().ordinal()) {
			return frontIcon;
		}

		return sideIcon;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (side == 3) {
			return frontIcon;
		}

		return sideIcon;
	}
}
