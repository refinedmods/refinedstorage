package storagecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import storagecraft.tile.TileCable;

public class BlockCable extends BlockBase
{
	public BlockCable()
	{
		super("cable");

		// float pixel = 1F / 16F;
		// @TODO: setBlockBounds(4 * pixel, 4 * pixel, 4 * pixel, 1 - 4 * pixel, 1 - 4 * pixel, 1 - 4 * pixel);
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
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isVisuallyOpaque()
	{
		return false;
	}
}
