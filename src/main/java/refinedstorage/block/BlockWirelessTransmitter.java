package refinedstorage.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageGui;
import refinedstorage.tile.TileWirelessTransmitter;

public class BlockWirelessTransmitter extends BlockMachine {
    public static final PropertyBool WORKING = PropertyBool.create("working");

    public BlockWirelessTransmitter() {
        super("wireless_transmitter");
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]
            {
                DIRECTION,
                CONNECTED,
                WORKING
            });
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getActualState(state, world, pos)
            .withProperty(WORKING, ((TileWirelessTransmitter) world.getTileEntity(pos)).isWorking());
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileWirelessTransmitter();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.WIRELESS_TRANSMITTER, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }
}
