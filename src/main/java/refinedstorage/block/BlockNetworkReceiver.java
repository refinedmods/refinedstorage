package refinedstorage.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import refinedstorage.tile.TileNetworkReceiver;

public class BlockNetworkReceiver extends BlockNode {
    public BlockNetworkReceiver() {
        super("network_receiver");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileNetworkReceiver();
    }
}
