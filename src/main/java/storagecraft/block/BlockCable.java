package storagecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import storagecraft.tile.TileCable;

import java.util.List;

public class BlockCable extends BlockBase
{
	public BlockCable()
	{
		super("cable");
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileCable();
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isFullCube() { return false; }

	@Override
	public boolean isVisuallyOpaque() { return false; }
}
