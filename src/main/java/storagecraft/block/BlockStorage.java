package storagecraft.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftBlocks;
import storagecraft.StorageCraftGUI;
import storagecraft.item.ItemBlockStorage;
import storagecraft.tile.TileStorage;

public class BlockStorage extends BlockMachine
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
			subItems.add(ItemBlockStorage.initNBT(new ItemStack(item, 1, i)));
		}
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]
		{
			DIRECTION,
			CONNECTED,
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

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileStorage();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(StorageCraft.INSTANCE, StorageCraftGUI.STORAGE, world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack)
	{
		super.onBlockPlacedBy(world, pos, state, player, stack);

		NBTTagCompound tag = stack.getTagCompound();

		if (tag != null && tag.hasKey(TileStorage.NBT_STORAGE))
		{
			((TileStorage) world.getTileEntity(pos)).setStorageTag((NBTTagCompound) tag.getTag(TileStorage.NBT_STORAGE));
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		List<ItemStack> drops = new ArrayList<ItemStack>();

		ItemStack stack = new ItemStack(StorageCraftBlocks.STORAGE, 1, StorageCraftBlocks.STORAGE.getMetaFromState(state));

		NBTTagCompound tag = new NBTTagCompound();

		tag.setTag(TileStorage.NBT_STORAGE, ((TileStorage) world.getTileEntity(pos)).getStorageTag());

		stack.setTagCompound(tag);

		drops.add(stack);

		return drops;
	}

	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		if (willHarvest)
		{
			return true;
		}

		return super.removedByPlayer(world, pos, player, willHarvest);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile)
	{
		super.harvestBlock(world, player, pos, state, tile);

		world.setBlockToAir(pos);
	}
}
