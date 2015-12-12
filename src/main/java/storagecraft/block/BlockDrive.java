package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import storagecraft.SC;
import storagecraft.tile.TileDrive;

public class BlockDrive extends BlockSC implements ITileEntityProvider {
	public BlockDrive() {
		super("drive");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			player.openGui(SC.INSTANCE, SC.GUI.DRIVE, world, x, y, z);
		}

		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileDrive();
	}
}
