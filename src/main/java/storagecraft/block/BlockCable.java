package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import storagecraft.tile.TileCable;

public class BlockCable extends BlockBase implements ITileEntityProvider {
	public BlockCable() {
		super("cable");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileCable();
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
}
