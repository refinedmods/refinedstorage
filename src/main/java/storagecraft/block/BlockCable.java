package storagecraft.block;

import java.util.List;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import storagecraft.tile.TileCable;

public class BlockCable extends BlockBase implements ITileEntityProvider
{
	public static final PropertyBool SENSITIVE = PropertyBool.create("sensitive");

	public BlockCable()
	{
		super("cable");
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]
		{
			SENSITIVE
		});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(SENSITIVE, meta == 1 ? true : false);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((Boolean) state.getValue(SENSITIVE)) ? 0 : 1;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileCable();
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List subItems)
	{
		for (int i = 0; i < 2; i++)
		{
			subItems.add(new ItemStack(item, 1, i));
		}
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
}
