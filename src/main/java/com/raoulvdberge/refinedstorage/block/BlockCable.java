package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.proxy.CapabilityNetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileCable;
import net.minecraft.block.properties.PropertyBool;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockCable extends BlockNode {
    protected static AxisAlignedBB createAABB(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        return new AxisAlignedBB((float) fromX / 16F, (float) fromY / 16F, (float) fromZ / 16F, (float) toX / 16F, (float) toY / 16F, (float) toZ / 16F);
    }

    protected static AxisAlignedBB CORE_AABB = createAABB(6, 6, 6, 10, 10, 10);
    protected static AxisAlignedBB NORTH_AABB = createAABB(6, 6, 0, 10, 10, 6);
    protected static AxisAlignedBB EAST_AABB = createAABB(10, 6, 6, 16, 10, 10);
    protected static AxisAlignedBB SOUTH_AABB = createAABB(6, 6, 10, 10, 10, 16);
    protected static AxisAlignedBB WEST_AABB = createAABB(0, 6, 6, 6, 10, 10);
    protected static AxisAlignedBB UP_AABB = createAABB(6, 10, 6, 10, 16, 10);
    protected static AxisAlignedBB DOWN_AABB = createAABB(6, 0, 6, 10, 6, 10);

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
        BlockStateContainer.Builder builder = super.createBlockStateBuilder();

        builder.add(NORTH)
            .add(EAST)
            .add(SOUTH)
            .add(WEST)
            .add(UP)
            .add(DOWN);

        if (getPlacementType() != null) {
            builder.add(DIRECTION);
        }

        return builder.build();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getActualState(state, world, pos)
            .withProperty(NORTH, hasConnectionWith(world, pos, EnumFacing.NORTH))
            .withProperty(EAST, hasConnectionWith(world, pos, EnumFacing.EAST))
            .withProperty(SOUTH, hasConnectionWith(world, pos, EnumFacing.SOUTH))
            .withProperty(WEST, hasConnectionWith(world, pos, EnumFacing.WEST))
            .withProperty(UP, hasConnectionWith(world, pos, EnumFacing.UP))
            .withProperty(DOWN, hasConnectionWith(world, pos, EnumFacing.DOWN));
    }

    private boolean hasConnectionWith(IBlockAccess world, BlockPos pos, EnumFacing direction) {
        TileEntity otherTile = world.getTileEntity(pos.offset(direction));
        EnumFacing otherTileSide = direction.getOpposite();

        return otherTile != null && otherTile.hasCapability(CapabilityNetworkNode.NETWORK_NODE_CAPABILITY, otherTileSide);
    }

    private boolean isInAABB(AxisAlignedBB aabb, float hitX, float hitY, float hitZ) {
        return hitX >= aabb.minX && hitX <= aabb.maxX && hitY >= aabb.minY && hitY <= aabb.maxY && hitZ >= aabb.minZ && hitZ <= aabb.maxZ;
    }

    protected boolean hitCablePart(IBlockState state, World world, BlockPos pos, float hitX, float hitY, float hitZ) {
        state = getActualState(state, world, pos);

        return isInAABB(CORE_AABB, hitX, hitY, hitZ) ||
            (state.getValue(NORTH) && isInAABB(NORTH_AABB, hitX, hitY, hitZ)) ||
            (state.getValue(EAST) && isInAABB(EAST_AABB, hitX, hitY, hitZ)) ||
            (state.getValue(SOUTH) && isInAABB(SOUTH_AABB, hitX, hitY, hitZ)) ||
            (state.getValue(WEST) && isInAABB(WEST_AABB, hitX, hitY, hitZ)) ||
            (state.getValue(UP) && isInAABB(UP_AABB, hitX, hitY, hitZ)) ||
            (state.getValue(DOWN) && isInAABB(DOWN_AABB, hitX, hitY, hitZ));
    }

    public List<AxisAlignedBB> getUnionizedCollisionBoxes(IBlockState state) {
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

    public List<AxisAlignedBB> getNonUnionizedCollisionBoxes(IBlockState state) {
        return Collections.emptyList();
    }

    public List<AxisAlignedBB> getCollisionBoxes(IBlockState state) {
        List<AxisAlignedBB> boxes = new ArrayList<>();

        boxes.addAll(getUnionizedCollisionBoxes(state));
        boxes.addAll(getNonUnionizedCollisionBoxes(state));

        return boxes;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn) {
        for (AxisAlignedBB aabb : getCollisionBoxes(this.getActualState(state, world, pos))) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        RSUtils.AdvancedRayTraceResult result = RSUtils.collisionRayTrace(pos, start, end, getCollisionBoxes(this.getActualState(state, world, pos)));

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

        if (getPlacementType() != null) {
            return state.withProperty(DIRECTION, getPlacementType().getFrom(facing, pos, entity));
        }

        return state;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public EnumPlacementType getPlacementType() {
        return null;
    }
}
