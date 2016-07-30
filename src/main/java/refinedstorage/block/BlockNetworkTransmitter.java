package refinedstorage.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import refinedstorage.tile.TileNetworkTransmitter;

public class BlockNetworkTransmitter extends BlockNode {
    public BlockNetworkTransmitter() {
        super("network_transmitter");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileNetworkTransmitter();
    }
}
