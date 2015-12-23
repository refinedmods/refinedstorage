package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.tile.TileSolderer;

public class BlockSolderer extends BlockBase implements ITileEntityProvider
{
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
}
