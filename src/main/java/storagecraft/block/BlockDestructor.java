package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import storagecraft.tile.TileDestructor;

public class BlockDestructor extends BlockBase implements ITileEntityProvider
{
	public BlockDestructor()
	{
		super("destructor");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileDestructor();
	}
}
