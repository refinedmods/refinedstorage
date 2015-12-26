package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import storagecraft.tile.TileMachine;

public abstract class BlockMachine extends BlockBase implements ITileEntityProvider
{
	public static final PropertyBool CONNECTED = PropertyBool.create("connected");

	public BlockMachine(String name)
	{
		super(name);
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]
		{
			CONNECTED
		});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 0;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return state.withProperty(CONNECTED, ((TileMachine) world.getTileEntity(pos)).isConnected());
	}
}
