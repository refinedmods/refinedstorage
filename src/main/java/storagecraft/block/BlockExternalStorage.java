package storagecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftGUI;
import storagecraft.tile.TileCable;
import storagecraft.tile.TileExternalStorage;

public class BlockExternalStorage extends BlockMachine
{
	public BlockExternalStorage()
	{
		super("external_storage");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileExternalStorage();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(StorageCraft.INSTANCE, StorageCraftGUI.EXTERNAL_STORAGE, world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}
}
