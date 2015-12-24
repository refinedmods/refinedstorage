package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.tile.TileSolderer;

public class BlockSolderer extends BlockBase implements ITileEntityProvider
{
	private IIcon sideIcon;
	private IIcon connectedIcon;
	private IIcon disconnectedIcon;

	public BlockSolderer()
	{
		super("solderer");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileSolderer();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(StorageCraft.INSTANCE, StorageCraft.GUI.SOLDERER, world, x, y, z);
		}

		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		connectedIcon = register.registerIcon("storagecraft:soldererConnected");
		disconnectedIcon = register.registerIcon("storagecraft:soldererDisconnected");
		sideIcon = register.registerIcon("storagecraft:side");
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		TileSolderer tile = (TileSolderer) world.getTileEntity(x, y, z);

		if (side == tile.getDirection().ordinal())
		{
			return tile.isConnected() ? connectedIcon : disconnectedIcon;
		}

		return sideIcon;
	}

	@Override
	public IIcon getIcon(int side, int damage)
	{
		if (side == 3)
		{
			return disconnectedIcon;
		}

		return sideIcon;
	}
}
