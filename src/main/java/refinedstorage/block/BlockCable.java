package refinedstorage.block;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkNode;
import refinedstorage.tile.TileBase;
import refinedstorage.tile.TileCable;

public class BlockCable extends BlockNode {
    private static final AxisAlignedBB CABLE_AABB = new AxisAlignedBB(4 * (1F / 16F), 4 * (1F / 16F), 4 * (1F / 16F), 1 - 4 * (1F / 16F), 1 - 4 * (1F / 16F), 1 - 4 * (1F / 16F));

    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");

    public BlockCable(String name) {
        super(name);

        setHardness(0.6F);
    }

    public BlockCable() {
        this("cable");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCable();
    }

    @Override
    protected BlockStateContainer.Builder createBlockStateBuilder() {
        return super.createBlockStateBuilder()
            .add(NORTH)
            .add(EAST)
            .add(SOUTH)
            .add(WEST)
            .add(UP)
            .add(DOWN);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getActualState(state, world, pos)
            .withProperty(NORTH, hasConnectionWith(world, pos, pos.north()))
            .withProperty(EAST, hasConnectionWith(world, pos, pos.east()))
            .withProperty(SOUTH, hasConnectionWith(world, pos, pos.south()))
            .withProperty(WEST, hasConnectionWith(world, pos, pos.west()))
            .withProperty(UP, hasConnectionWith(world, pos, pos.up()))
            .withProperty(DOWN, hasConnectionWith(world, pos, pos.down()));
    }

    private boolean hasConnectionWith(IBlockAccess world, BlockPos basePos, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof INetworkMaster || tile instanceof INetworkNode) {
            // Do not render a cable extension to on this position when we have a direction (like an exporter, importer or external storage)
            if (getPlacementType() != null) {
                return ((TileBase) world.getTileEntity(basePos)).getFacingTile() != tile;
            }

            return true;
        }

        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return CABLE_AABB;
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
        return null;
    }
}
