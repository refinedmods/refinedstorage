package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.tile.TileExporter;

public class BlockExporter extends BlockBase implements ITileEntityProvider
{
	private IIcon frontIcon;
	private IIcon sideIcon;

	public BlockExporter()
	{
		super("exporter");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileExporter();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(StorageCraft.INSTANCE, StorageCraft.GUI.EXPORTER, world, x, y, z);
		}

		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		frontIcon = register.registerIcon("storagecraft:exporter");
		sideIcon = register.registerIcon("storagecraft:side");
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		TileExporter tile = (TileExporter) world.getTileEntity(x, y, z);

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
