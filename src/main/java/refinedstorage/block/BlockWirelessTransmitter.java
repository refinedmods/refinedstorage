package refinedstorage.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import refinedstorage.tile.TileWirelessTransmitter;

public class BlockWirelessTransmitter extends BlockMachine {
    public BlockWirelessTransmitter() {
        super("wireless_transmitter");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileWirelessTransmitter();
    }
}
