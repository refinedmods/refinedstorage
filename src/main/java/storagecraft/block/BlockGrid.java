package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.tile.TileGrid;

public class BlockGrid extends BlockBase implements ITileEntityProvider {
	private IIcon sideIcon;
	private IIcon connectedIcon;
	private IIcon disconnectedIcon;

	public BlockGrid() {
		super("grid");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileGrid();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			player.openGui(StorageCraft.INSTANCE, StorageCraft.GUI.GRID, world, x, y, z);
		}

		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		connectedIcon = register.registerIcon("storagecraft:gridConnected");
		disconnectedIcon = register.registerIcon("storagecraft:gridDisconnected");
		sideIcon = register.registerIcon("storagecraft:generic");
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileGrid tile = (TileGrid) world.getTileEntity(x, y, z);

		if (side == tile.getDirection().ordinal()) {
			return tile.isConnected() ? connectedIcon : disconnectedIcon;
		}

		return sideIcon;
	}

	@Override
	public IIcon getIcon(int side, int damage) {
		if (side == 3) {
			return disconnectedIcon;
		}

		return sideIcon;
	}
}
