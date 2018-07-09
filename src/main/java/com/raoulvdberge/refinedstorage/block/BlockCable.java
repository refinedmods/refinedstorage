package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.ICoverable;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.Cover;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;
import com.raoulvdberge.refinedstorage.block.property.PropertyObject;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import com.raoulvdberge.refinedstorage.render.collision.AdvancedRayTraceResult;
import com.raoulvdberge.refinedstorage.render.collision.AdvancedRayTracer;
import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.render.collision.constants.ConstantsCable;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.TileCable;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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

import java.util.ArrayList;
import java.util.List;

public class BlockCable extends BlockNode {
    public static final PropertyObject<Cover> COVER_NORTH = new PropertyObject<>("cover_north", Cover.class);
    public static final PropertyObject<Cover> COVER_EAST = new PropertyObject<>("cover_east", Cover.class);
    public static final PropertyObject<Cover> COVER_SOUTH = new PropertyObject<>("cover_south", Cover.class);
    public static final PropertyObject<Cover> COVER_WEST = new PropertyObject<>("cover_west", Cover.class);
    public static final PropertyObject<Cover> COVER_UP = new PropertyObject<>("cover_up", Cover.class);
    public static final PropertyObject<Cover> COVER_DOWN = new PropertyObject<>("cover_down", Cover.class);

    private static final PropertyBool NORTH = PropertyBool.create("north");
    private static final PropertyBool EAST = PropertyBool.create("east");
    private static final PropertyBool SOUTH = PropertyBool.create("south");
    private static final PropertyBool WEST = PropertyBool.create("west");
    private static final PropertyBool UP = PropertyBool.create("up");
    private static final PropertyBool DOWN = PropertyBool.create("down");

    public BlockCable(IBlockInfo info) {
        super(info);
    }

    public BlockCable() {
        super(createBuilder("cable").tileEntity(TileCable::new).create());
    }

    static BlockInfoBuilder createBuilder(String id) {
        return BlockInfoBuilder.forId(id).material(Material.GLASS).soundType(SoundType.GLASS).hardness(0.35F);
    }

    public boolean hasConnectivityState() {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return super.createStateBuilder()
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

            if (cover != null && !cover.getType().isHollow()) {
                return false;
            }
        }

        TileEntity otherTile = world.getTileEntity(pos.offset(direction));

        if (otherTile instanceof TileNode && ((TileNode) otherTile).getNode() instanceof ICoverable) {
            Cover cover = ((ICoverable) ((TileNode) otherTile).getNode()).getCoverManager().getCover(direction.getOpposite());

            if (cover != null && !cover.getType().isHollow()) {
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

    protected boolean canAccessGui(IBlockState state, World world, BlockPos pos, float hitX, float hitY, float hitZ) {
        state = getActualState(state, world, pos);

        for (CollisionGroup group : getCollisions(world.getTileEntity(pos), state)) {
            if (group.canAccessGui()) {
                for (AxisAlignedBB aabb : group.getItems()) {
                    if (RenderUtils.isInBounds(aabb, hitX, hitY, hitZ)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public List<CollisionGroup> getCollisions(TileEntity tile, IBlockState state) {
        List<CollisionGroup> groups = getCoverCollisions(tile);

        groups.add(ConstantsCable.CORE);

        if (state.getValue(NORTH)) {
            groups.add(ConstantsCable.NORTH);
        }

        if (state.getValue(EAST)) {
            groups.add(ConstantsCable.EAST);
        }

        if (state.getValue(SOUTH)) {
            groups.add(ConstantsCable.SOUTH);
        }

        if (state.getValue(WEST)) {
            groups.add(ConstantsCable.WEST);
        }

        if (state.getValue(UP)) {
            groups.add(ConstantsCable.UP);
        }

        if (state.getValue(DOWN)) {
            groups.add(ConstantsCable.DOWN);
        }

        return groups;
    }

    private List<CollisionGroup> getCoverCollisions(TileEntity tile) {
        List<CollisionGroup> groups = new ArrayList<>();

        if (tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable) {
            CoverManager coverManager = ((ICoverable) ((TileNode) tile).getNode()).getCoverManager();

            Cover coverNorth = coverManager.getCover(EnumFacing.NORTH);
            Cover coverEast = coverManager.getCover(EnumFacing.EAST);
            Cover coverSouth = coverManager.getCover(EnumFacing.SOUTH);
            Cover coverWest = coverManager.getCover(EnumFacing.WEST);
            Cover coverUp = coverManager.getCover(EnumFacing.UP);
            Cover coverDown = coverManager.getCover(EnumFacing.DOWN);

            if (coverNorth != null) {
                groups.add(new CollisionGroup().addItem(RenderUtils.getBounds(
                    coverWest != null ? 2 : 0, coverDown != null ? 2 : 0, 0,
                    coverEast != null ? 14 : 16, coverUp != null ? 14 : 16, 2
                )));

                if (!coverNorth.getType().isHollow()) {
                    groups.add(ConstantsCable.HOLDER_NORTH);
                }
            }

            if (coverEast != null) {
                groups.add(new CollisionGroup().addItem(RenderUtils.getBounds(
                    14, coverDown != null ? 2 : 0, 0,
                    16, coverUp != null ? 14 : 16, 16
                )));

                if (!coverEast.getType().isHollow()) {
                    groups.add(ConstantsCable.HOLDER_EAST);
                }
            }

            if (coverSouth != null) {
                groups.add(new CollisionGroup().addItem(RenderUtils.getBounds(
                    coverEast != null ? 14 : 16, coverDown != null ? 2 : 0, 16,
                    coverWest != null ? 2 : 0, coverUp != null ? 14 : 16, 14
                )));

                if (!coverSouth.getType().isHollow()) {
                    groups.add(ConstantsCable.HOLDER_SOUTH);
                }
            }

            if (coverWest != null) {
                groups.add(new CollisionGroup().addItem(RenderUtils.getBounds(
                    0, coverDown != null ? 2 : 0, 0,
                    2, coverUp != null ? 14 : 16, 16
                )));

                if (!coverWest.getType().isHollow()) {
                    groups.add(ConstantsCable.HOLDER_WEST);
                }
            }

            if (coverUp != null) {
                groups.add(new CollisionGroup().addItem(RenderUtils.getBounds(
                    0, 14, 0,
                    16, 16, 16
                )));

                if (!coverUp.getType().isHollow()) {
                    groups.add(ConstantsCable.HOLDER_UP);
                }
            }

            if (coverDown != null) {
                groups.add(new CollisionGroup().addItem(RenderUtils.getBounds(
                    0, 0, 0,
                    16, 2, 16
                )));

                if (!coverDown.getType().isHollow()) {
                    groups.add(ConstantsCable.HOLDER_DOWN);
                }
            }
        }

        return groups;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_) {
        for (CollisionGroup group : getCollisions(world.getTileEntity(pos), this.getActualState(state, world, pos))) {
            for (AxisAlignedBB aabb : group.getItems()) {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        AdvancedRayTraceResult result = AdvancedRayTracer.rayTrace(pos, start, end, getCollisions(world.getTileEntity(pos), this.getActualState(state, world, pos)));

        return result != null ? result.getHit() : null;
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
}
