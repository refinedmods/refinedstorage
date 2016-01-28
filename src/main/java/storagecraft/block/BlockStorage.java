package storagecraft.block;

import java.util.List;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import static storagecraft.block.BlockBase.DIRECTION;

public class BlockStorage extends BlockBase
{
	public static final PropertyEnum TYPE = PropertyEnum.create("type", EnumStorageType.class);

	public BlockStorage()
	{
		super("storage");
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List subItems)
	{
		for (int i = 0; i <= 4; i++)
		{
			subItems.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]
		{
			DIRECTION,
			TYPE
		});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(TYPE, EnumStorageType.getById(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumStorageType) state.getValue(TYPE)).getId();
	}
}
