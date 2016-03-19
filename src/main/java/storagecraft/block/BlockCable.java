package storagecraft.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.tile.TileCable;

public class BlockCable extends BlockBase
{
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");

	public BlockCable()
	{
		super("cable");
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]
		{
			DIRECTION,
			NORTH,
			EAST,
			SOUTH,
			WEST,
			UP,
			DOWN,
		});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return super.getActualState(state, world, pos)
			.withProperty(NORTH, TileCable.hasConnectionWith(world, pos.north()))
			.withProperty(EAST, TileCable.hasConnectionWith(world, pos.east()))
			.withProperty(SOUTH, TileCable.hasConnectionWith(world, pos.south()))
			.withProperty(WEST, TileCable.hasConnectionWith(world, pos.west()))
			.withProperty(UP, TileCable.hasConnectionWith(world, pos.up()))
			.withProperty(DOWN, TileCable.hasConnectionWith(world, pos.down()));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		float pixel = 1F / 16F;

		return new AxisAlignedBB(4 * pixel, 4 * pixel, 4 * pixel, 1 - 4 * pixel, 1 - 4 * pixel, 1 - 4 * pixel);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
	{
		return getBoundingBox(state, world, pos);
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
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
}
