package storagecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import storagecraft.StorageCraft;
import storagecraft.StorageCraftGUI;
import storagecraft.tile.TileImporter;

public class BlockImporter extends BlockMachine
{
	public BlockImporter()
	{
		super("importer");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileImporter();
	}

	@Override
	public boolean onBlockActivated(World world, net.minecraft.util.math.BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			player.openGui(StorageCraft.INSTANCE, StorageCraftGUI.IMPORTER, world, pos.getX(), pos.getY(), pos.getZ());
		}

		return true;
	}
}
