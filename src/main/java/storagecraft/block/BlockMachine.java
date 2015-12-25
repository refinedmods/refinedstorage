package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;

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
		return getDefaultState().withProperty(CONNECTED, meta == 1 ? true : false);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((Boolean) state.getValue(CONNECTED)) ? 0 : 1;
	}
}
