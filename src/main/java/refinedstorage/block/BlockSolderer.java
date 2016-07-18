package refinedstorage.block;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageGui;
import refinedstorage.tile.TileSolderer;

public class BlockSolderer extends BlockNode {
    public static final AxisAlignedBB AABB_SOLDERER = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 14D / 16D, 1.0D);

    public static final PropertyBool WORKING = PropertyBool.create("working");

    public BlockSolderer() {
        super("solderer");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileSolderer();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.SOLDERER, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB_SOLDERER;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return super.createBlockStateBuilder()
            .add(WORKING)
            .build();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getActualState(state, world, pos)
            .withProperty(WORKING, ((TileSolderer) world.getTileEntity(pos)).isWorking());
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumPlacementType getPlacementType() {
        return EnumPlacementType.HORIZONTAL;
    }
}
