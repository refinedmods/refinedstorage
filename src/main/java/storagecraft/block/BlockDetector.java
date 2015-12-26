package storagecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftGUI;
import storagecraft.tile.TileDetector;

// @TODO: This texture behaves differently
public class BlockDetector extends BlockMachine
{
	public BlockDetector()
	{
		super("detector");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileDetector();
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
