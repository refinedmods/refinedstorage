package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.tile.TileGrid;
import storagecraft.tile.TileSC;

public class BlockGrid extends BlockSC implements ITileEntityProvider {
	private IIcon sideIcon;
	private IIcon icon;

	public BlockGrid() {
		super("grid");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileGrid();
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		icon = register.registerIcon("storagecraft:grid");
		sideIcon = register.registerIcon("storagecraft:generic");
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileSC tile = (TileSC) world.getTileEntity(x, y, z);

		if (side == tile.getDirection().getOpposite().ordinal()) {
			return icon;
		}

		return sideIcon;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if (side == 3) {
			return icon;
		}

		return sideIcon;
	}
}
