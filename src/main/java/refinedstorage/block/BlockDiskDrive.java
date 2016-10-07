package refinedstorage.block;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
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
import refinedstorage.RS;
import refinedstorage.RSGui;
import refinedstorage.tile.TileDiskDrive;

public class BlockDiskDrive extends BlockNode {
    private static final PropertyInteger STORED = PropertyInteger.create("stored", 0, 7);

    private static final PropertyBool FILLED_0 = PropertyBool.create("filled_0");
    private static final PropertyBool FILLED_1 = PropertyBool.create("filled_1");
    private static final PropertyBool FILLED_2 = PropertyBool.create("filled_2");
    private static final PropertyBool FILLED_3 = PropertyBool.create("filled_3");
    private static final PropertyBool FILLED_4 = PropertyBool.create("filled_4");
    private static final PropertyBool FILLED_5 = PropertyBool.create("filled_5");
    private static final PropertyBool FILLED_6 = PropertyBool.create("filled_6");
    private static final PropertyBool FILLED_7 = PropertyBool.create("filled_7");

    public BlockDiskDrive() {
        super("disk_drive");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileDiskDrive();
    }

    @Override
    public BlockStateContainer createBlockState() {
        return createBlockStateBuilder()
            .add(STORED)
            .add(FILLED_0)
            .add(FILLED_1)
            .add(FILLED_2)
            .add(FILLED_3)
            .add(FILLED_4)
            .add(FILLED_5)
            .add(FILLED_6)
            .add(FILLED_7)
            .build();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileDiskDrive diskDrive = (TileDiskDrive) world.getTileEntity(pos);

        state = super.getActualState(state, world, pos);
        state = state.withProperty(STORED, Math.max(0, diskDrive.getStoredForDisplay()));
        state = state.withProperty(FILLED_0, diskDrive.getFilled()[0]);
        state = state.withProperty(FILLED_1, diskDrive.getFilled()[1]);
        state = state.withProperty(FILLED_2, diskDrive.getFilled()[2]);
        state = state.withProperty(FILLED_3, diskDrive.getFilled()[3]);
        state = state.withProperty(FILLED_4, diskDrive.getFilled()[4]);
        state = state.withProperty(FILLED_5, diskDrive.getFilled()[5]);
        state = state.withProperty(FILLED_6, diskDrive.getFilled()[6]);
        state = state.withProperty(FILLED_7, diskDrive.getFilled()[7]);

        return state;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(RS.INSTANCE, RSGui.DISK_DRIVE, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        ((TileDiskDrive) world.getTileEntity(pos)).onBreak();

        super.breakBlock(world, pos, state);
    }
}
