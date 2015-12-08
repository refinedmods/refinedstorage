package storagecraft.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import storagecraft.tile.TileGrid;

public class BlockGrid extends BlockSC implements ITileEntityProvider {
	public BlockGrid() {
		super("grid");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileGrid();
	}
}
