package refinedstorage.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import refinedstorage.tile.TileMachine;

public abstract class BlockMachine extends BlockBase
{
	public static final PropertyBool CONNECTED = PropertyBool.create("connected");

	public BlockMachine(String name)
	{
		super(name);
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]
		{
			DIRECTION,
			CONNECTED
		});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return super.getActualState(state, world, pos)
			.withProperty(CONNECTED, ((TileMachine) world.getTileEntity(pos)).isConnected());
	}
}
