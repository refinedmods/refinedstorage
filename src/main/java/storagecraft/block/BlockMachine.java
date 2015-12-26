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
