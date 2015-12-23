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

public class BlockController extends BlockBase implements ITileEntityProvider
{
	private IIcon sideIcon;
	private IIcon[] icons = new IIcon[9];

	public BlockController()
	{
		super("controller");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileController();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(StorageCraft.INSTANCE, StorageCraft.GUI.CONTROLLER, world, x, y, z);
		}

		return true;
	}

	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta)
	{
		((TileController) world.getTileEntity(x, y, z)).onDestroyed();

		super.onBlockPreDestroy(world, x, y, z, meta);
	}

	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side)
	{
		TileController tile = (TileController) world.getTileEntity(x, y, z);

		return tile.getEnergyScaled(15);
	}

	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		for (int i = 0; i <= 8; ++i)
		{
			icons[i] = register.registerIcon("storagecraft:controller" + i);
		}

		sideIcon = register.registerIcon("storagecraft:generic");
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		TileController tile = (TileController) world.getTileEntity(x, y, z);

		if (side == tile.getDirection().ordinal())
		{
			return icons[tile.getEnergyScaled(8)];
		}

		return sideIcon;
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		if (side == 3)
		{
			return icons[0];
		}

		return sideIcon;
	}
}
