package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.tile.TileDetector;

public class BlockDetector extends BlockBase implements ITileEntityProvider
{
	private IIcon frontIcon;
	private IIcon sideIcon;

	public BlockDetector()
	{
		super("detector");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(StorageCraft.INSTANCE, StorageCraft.GUI.DETECTOR, world, x, y, z);
		}

		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileDetector();
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
	{
		return isProvidingStrongPower(world, x, y, z, side);
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
	{
		TileDetector detector = (TileDetector) world.getTileEntity(x, y, z);

		return detector.providesPower() ? 15 : 0;
	}

	@Override
	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		frontIcon = register.registerIcon("storagecraft:detector");
		sideIcon = register.registerIcon("storagecraft:generic");
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		TileDetector tile = (TileDetector) world.getTileEntity(x, y, z);

		if (side == tile.getDirection().ordinal())
		{
			return frontIcon;
		}

		return sideIcon;
	}

	@Override
	public IIcon getIcon(int side, int meta)
	{
		if (side == 3)
		{
			return frontIcon;
		}

		return sideIcon;
	}
}
