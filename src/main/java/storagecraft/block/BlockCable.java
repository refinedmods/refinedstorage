package storagecraft.block;

import java.util.List;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
	public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
		for (int i = 0; i < 2; i++) {
			subItems.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
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
