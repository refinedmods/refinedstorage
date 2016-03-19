package storagecraft.block;

import java.util.List;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftGUI;
import storagecraft.tile.TileGrid;

public class BlockGrid extends BlockMachine
{
	public static final PropertyEnum TYPE = PropertyEnum.create("type", EnumGridType.class);

	public BlockGrid()
	{
		super("grid");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileGrid();
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List subItems)
	{
		for (int i = 0; i <= 1; i++)
		{
			subItems.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]
		{
			DIRECTION,
			CONNECTED,
			TYPE
		});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(TYPE, meta == 0 ? EnumGridType.NORMAL : EnumGridType.CRAFTING);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(TYPE) == EnumGridType.NORMAL ? 0 : 1;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(StorageCraft.INSTANCE, StorageCraftGUI.GRID, world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}
}
