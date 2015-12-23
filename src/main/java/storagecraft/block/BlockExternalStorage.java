package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.tile.TileExternalStorage;

public class BlockExternalStorage extends BlockBase implements ITileEntityProvider
{
	private IIcon frontIcon;
	private IIcon sideIcon;

	public BlockExternalStorage()
	{
		super("externalStorage");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileExternalStorage();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(StorageCraft.INSTANCE, StorageCraft.GUI.EXTERNAL_STORAGE, world, x, y, z);
		}

		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		frontIcon = register.registerIcon("storagecraft:externalStorage");
		sideIcon = register.registerIcon("storagecraft:generic");
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		TileExternalStorage tile = (TileExternalStorage) world.getTileEntity(x, y, z);

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
