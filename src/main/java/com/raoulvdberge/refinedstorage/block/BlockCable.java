package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.ICoverable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.Cover;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.TileCable;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockCable extends BlockNode {
    public static final PropertyObject<Cover> COVER_NORTH = new PropertyObject<>("cover_north", Cover.class);
    public static final PropertyObject<Cover> COVER_EAST = new PropertyObject<>("cover_east", Cover.class);
    public static final PropertyObject<Cover> COVER_SOUTH = new PropertyObject<>("cover_south", Cover.class);
    public static final PropertyObject<Cover> COVER_WEST = new PropertyObject<>("cover_west", Cover.class);
    public static final PropertyObject<Cover> COVER_UP = new PropertyObject<>("cover_up", Cover.class);
    public static final PropertyObject<Cover> COVER_DOWN = new PropertyObject<>("cover_down", Cover.class);

    public static final AxisAlignedBB HOLDER_NORTH_AABB = RenderUtils.getBounds(7, 7, 2, 9, 9, 6);
    public static final AxisAlignedBB HOLDER_EAST_AABB = RenderUtils.getBounds(10, 7, 7, 14, 9, 9);
    public static final AxisAlignedBB HOLDER_SOUTH_AABB = RenderUtils.getBounds(7, 7, 10, 9, 9, 14);
    public static final AxisAlignedBB HOLDER_WEST_AABB = RenderUtils.getBounds(2, 7, 7, 6, 9, 9);
    public static final AxisAlignedBB HOLDER_UP_AABB = RenderUtils.getBounds(7, 10, 7, 9, 14, 9);
    public static final AxisAlignedBB HOLDER_DOWN_AABB = RenderUtils.getBounds(7, 2, 7, 9, 6, 9);

    public static final AxisAlignedBB CORE_AABB = RenderUtils.getBounds(6, 6, 6, 10, 10, 10);
    private static final AxisAlignedBB NORTH_AABB = RenderUtils.getBounds(6, 6, 0, 10, 10, 6);
    private static final AxisAlignedBB EAST_AABB = RenderUtils.getBounds(10, 6, 6, 16, 10, 10);
    private static final AxisAlignedBB SOUTH_AABB = RenderUtils.getBounds(6, 6, 10, 10, 10, 16);
    private static final AxisAlignedBB WEST_AABB = RenderUtils.getBounds(0, 6, 6, 6, 10, 10);
    private static final AxisAlignedBB UP_AABB = RenderUtils.getBounds(6, 10, 6, 10, 16, 10);
    private static final AxisAlignedBB DOWN_AABB = RenderUtils.getBounds(6, 0, 6, 10, 6, 10);

    protected static final PropertyBool NORTH = PropertyBool.create("north");
    protected static final PropertyBool EAST = PropertyBool.create("east");
    protected static final PropertyBool SOUTH = PropertyBool.create("south");
    protected static final PropertyBool WEST = PropertyBool.create("west");
    protected static final PropertyBool UP = PropertyBool.create("up");
    protected static final PropertyBool DOWN = PropertyBool.create("down");

    public BlockCable(String name) {
        super(name);
    }

    public BlockCable() {
        this("cable");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCable();
    }

    public boolean hasConnectivityState() {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return super.createBlockStateBuilder()
            .add(NORTH)
            .add(EAST)
            .add(SOUTH)
            .add(WEST)
            .add(UP)
            .add(DOWN)
            .add(COVER_NORTH)
            .add(COVER_EAST)
            .add(COVER_SOUTH)
            .add(COVER_WEST)
            .add(COVER_UP)
            .add(COVER_DOWN)
            .build();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        state = super.getActualState(state, world, pos)
            .withProperty(NORTH, hasConnectionWith(world, pos, this, tile, EnumFacing.NORTH))
            .withProperty(EAST, hasConnectionWith(world, pos, this, tile, EnumFacing.EAST))
            .withProperty(SOUTH, hasConnectionWith(world, pos, this, tile, EnumFacing.SOUTH))
            .withProperty(WEST, hasConnectionWith(world, pos, this, tile, EnumFacing.WEST))
            .withProperty(UP, hasConnectionWith(world, pos, this, tile, EnumFacing.UP))
            .withProperty(DOWN, hasConnectionWith(world, pos, this, tile, EnumFacing.DOWN));

        return state;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState s = super.getExtendedState(state, world, pos);

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable) {
            s = ((IExtendedBlockState) s).withProperty(COVER_NORTH, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(EnumFacing.NORTH));
            s = ((IExtendedBlockState) s).withProperty(COVER_EAST, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(EnumFacing.EAST));
            s = ((IExtendedBlockState) s).withProperty(COVER_SOUTH, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(EnumFacing.SOUTH));
            s = ((IExtendedBlockState) s).withProperty(COVER_WEST, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(EnumFacing.WEST));
            s = ((IExtendedBlockState) s).withProperty(COVER_UP, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(EnumFacing.UP));
            s = ((IExtendedBlockState) s).withProperty(COVER_DOWN, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(EnumFacing.DOWN));
        }

        return s;
    }

    private static boolean hasConnectionWith(IBlockAccess world, BlockPos pos, BlockBase block, TileEntity tile, EnumFacing direction) {
        if (!(tile instanceof TileNode)) {
            return false;
        }

        INetworkNode node = ((TileNode) tile).getNode();

        if (node instanceof ICoverable) {
            Cover cover = ((ICoverable) node).getCoverManager().getCover(direction);

            if (cover != null && !cover.isHollow()) {
                return false;
            }
        }

        TileEntity otherTile = world.getTileEntity(pos.offset(direction));

        if (otherTile instanceof TileNode && ((TileNode) otherTile).getNode() instanceof ICoverable) {
            Cover cover = ((ICoverable) ((TileNode) otherTile).getNode()).getCoverManager().getCover(direction.getOpposite());

            if (cover != null && !cover.isHollow()) {
                return false;
            }
        }

        if (otherTile != null && otherTile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, direction.getOpposite())) {
            // Prevent the block adding connections in itself
            // For example: importer cable connection on the importer face
            if (block.getDirection() != null && ((TileBase) tile).getDirection() == direction) {
                return false;
            }

            return true;
        }

        return false;
    }

    protected boolean hitCablePart(IBlockState state, World world, BlockPos pos, float hitX, float hitY, float hitZ) {
        state = getActualState(state, world, pos);

        if ((RenderUtils.isInBounds(CORE_AABB, hitX, hitY, hitZ)) ||
            (state.getValue(NORTH) && RenderUtils.isInBounds(NORTH_AABB, hitX, hitY, hitZ)) ||
            (state.getValue(EAST) && RenderUtils.isInBounds(EAST_AABB, hitX, hitY, hitZ)) ||
            (state.getValue(SOUTH) && RenderUtils.isInBounds(SOUTH_AABB, hitX, hitY, hitZ)) ||
            (state.getValue(WEST) && RenderUtils.isInBounds(WEST_AABB, hitX, hitY, hitZ)) ||
            (state.getValue(UP) && RenderUtils.isInBounds(UP_AABB, hitX, hitY, hitZ)) ||
            (state.getValue(DOWN) && RenderUtils.isInBounds(DOWN_AABB, hitX, hitY, hitZ))) {
            return true;
        }

        List<AxisAlignedBB> coverAabbs = getCoverCollisions(world.getTileEntity(pos));

        for (AxisAlignedBB coverAabb : coverAabbs) {
            if (RenderUtils.isInBounds(coverAabb, hitX, hitY, hitZ)) {
                return true;
            }
        }

        return false;
    }

    public List<AxisAlignedBB> getCombinedCollisionBoxes(IBlockState state) {
        List<AxisAlignedBB> boxes = new ArrayList<>();

        boxes.add(CORE_AABB);

        if (state.getValue(NORTH)) {
            boxes.add(NORTH_AABB);
        }

        if (state.getValue(EAST)) {
            boxes.add(EAST_AABB);
        }

        if (state.getValue(SOUTH)) {
            boxes.add(SOUTH_AABB);
        }

        if (state.getValue(WEST)) {
            boxes.add(WEST_AABB);
        }

        if (state.getValue(UP)) {
            boxes.add(UP_AABB);
        }

        if (state.getValue(DOWN)) {
            boxes.add(DOWN_AABB);
        }

        return boxes;
    }

    public List<AxisAlignedBB> getCollisionBoxes(TileEntity tile, IBlockState state) {
        return getCoverCollisions(tile);
    }

    private List<AxisAlignedBB> getCoverCollisions(TileEntity tile) {
        List<AxisAlignedBB> boxes = new ArrayList<>();

        if (tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable) {
            CoverManager coverManager = ((ICoverable) ((TileNode) tile).getNode()).getCoverManager();

            Cover coverNorth = coverManager.getCover(EnumFacing.NORTH);
            Cover coverEast = coverManager.getCover(EnumFacing.EAST);
            Cover coverSouth = coverManager.getCover(EnumFacing.SOUTH);
            Cover coverWest = coverManager.getCover(EnumFacing.WEST);
            Cover coverUp = coverManager.getCover(EnumFacing.UP);
            Cover coverDown = coverManager.getCover(EnumFacing.DOWN);

            if (coverNorth != null) {
                boxes.add(RenderUtils.getBounds(
                    coverWest != null ? 2 : 0, coverDown != null ? 2 : 0, 0,
                    coverEast != null ? 14 : 16, coverUp != null ? 14 : 16, 2
                ));

                if (!coverNorth.isHollow()) {
                    boxes.add(HOLDER_NORTH_AABB);
                }
            }

            if (coverEast != null) {
                boxes.add(RenderUtils.getBounds(
                    14, coverDown != null ? 2 : 0, 0,
                    16, coverUp != null ? 14 : 16, 16
                ));

                if (!coverEast.isHollow()) {
                    boxes.add(HOLDER_EAST_AABB);
                }
            }

            if (coverSouth != null) {
                boxes.add(RenderUtils.getBounds(
                    coverEast != null ? 14 : 16, coverDown != null ? 2 : 0, 16,
                    coverWest != null ? 2 : 0, coverUp != null ? 14 : 16, 14
                ));

                if (!coverSouth.isHollow()) {
                    boxes.add(HOLDER_SOUTH_AABB);
                }
            }

            if (coverWest != null) {
                boxes.add(RenderUtils.getBounds(
                    0, coverDown != null ? 2 : 0, 0,
                    2, coverUp != null ? 14 : 16, 16
                ));

                if (!coverWest.isHollow()) {
                    boxes.add(HOLDER_WEST_AABB);
                }
            }

            if (coverUp != null) {
                boxes.add(RenderUtils.getBounds(
                    0, 14, 0,
                    16, 16, 16
                ));

                if (!coverUp.isHollow()) {
                    boxes.add(HOLDER_UP_AABB);
                }
            }

            if (coverDown != null) {
                boxes.add(RenderUtils.getBounds(
                    0, 0, 0,
                    16, 2, 16
                ));

                if (!coverDown.isHollow()) {
                    boxes.add(HOLDER_DOWN_AABB);
                }
            }
        }

        return boxes;
    }

    private List<AxisAlignedBB> getAllCollisionBoxes(TileEntity tile, IBlockState state) {
        List<AxisAlignedBB> boxes = new ArrayList<>();

        boxes.addAll(getCombinedCollisionBoxes(state));
        boxes.addAll(getCollisionBoxes(tile, state));

        return boxes;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_) {
        for (AxisAlignedBB aabb : getAllCollisionBoxes(world.getTileEntity(pos), this.getActualState(state, world, pos))) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        RenderUtils.AdvancedRayTraceResult result = RenderUtils.collisionRayTrace(pos, start, end, getAllCollisionBoxes(world.getTileEntity(pos), this.getActualState(state, world, pos)));

        return result != null ? result.hit : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase entity) {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, entity);

        if (getDirection() != null) {
            return state.withProperty(getDirection().getProperty(), getDirection().getFrom(facing, pos, entity));
        }

        return state;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @Nullable
    public Direction getDirection() {
        return null;
    }
}
