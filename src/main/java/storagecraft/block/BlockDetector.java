package storagecraft.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftGUI;
import storagecraft.tile.TileDetector;

public class BlockDetector extends BlockMachine
{
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockDetector()
	{
		super("detector");
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]
			{
				DIRECTION,
				CONNECTED,
				POWERED
			});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return super.getActualState(state, world, pos)
			.withProperty(POWERED, ((TileDetector) world.getTileEntity(pos)).isPowered());
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileDetector();
	}

	@Override
	public int getWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side)
	{
		TileDetector detector = (TileDetector) world.getTileEntity(pos);

		if (detector.getDirection() == side.getOpposite())
		{
			return detector.isPowered() ? 15 : 0;
		}

		return 0;
	}

	@Override
	public int getStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side)
	{
		return getWeakPower(world, pos, state, side);
	}

	public boolean canProvidePower()
	{
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(StorageCraft.INSTANCE, StorageCraftGUI.DETECTOR, world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}
}
