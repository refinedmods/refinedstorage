package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;
import com.raoulvdberge.refinedstorage.item.blockitem.ItemBlockBase;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
import com.raoulvdberge.refinedstorage.util.CollisionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public abstract class BlockBase extends Block {
    private static final CollisionGroup DEFAULT_COLLISION_GROUP = new CollisionGroup().addItem(new AxisAlignedBB(0, 0, 0, 1, 1, 1)).setCanAccessGui(true);
    private static final List<CollisionGroup> DEFAULT_COLLISION_GROUPS = Collections.singletonList(DEFAULT_COLLISION_GROUP);

    protected final IBlockInfo info;

    public BlockBase(IBlockInfo info) {
        super(Block.Properties
            .create(info.getMaterial())
            .hardnessAndResistance(info.getHardness())
            .sound(info.getSoundType())
        );

        this.info = info;

        setRegistryName(info.getId());
    }

    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
    }

    @Override
    public String getTranslationKey() {
        return "block." + info.getId().toString();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        if (getDirection() != null) {
            builder.add(getDirection().getProperty());
        }
    }

    public Item createItem() {
        return new ItemBlockBase(this);
    }
/* TODO
    @Override
    public boolean rotateBlock(World world, BlockPos pos, Direction axis) {
        if (!world.isRemote && getDirection() != null) {
            TileBase tile = (TileBase) world.getTileEntity(pos);

            Direction newDirection = getDirection().cycle(tile.getDirection());

            tile.setDirection(newDirection);

            WorldUtils.updateBlock(world, pos);

            return true;
        }

        return false;
    } */

/* TODO
    @Override
    public void breakBlock(World world, BlockPos pos, BlockState state) {
        dropContents(world, pos);
        removeTile(world, pos, state);
    }

    void removeTile(World world, BlockPos pos, BlockState state) {
        if (hasTileEntity(state)) {
            world.removeTileEntity(pos);
        }
    }

    void dropContents(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileBase && ((TileBase) tile).getDrops() != null) {
            WorldUtils.dropInventory(world, pos, ((TileBase) tile).getDrops());
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity tile, ItemStack stack) {
        super.harvestBlock(world, player, pos, state, tile, stack);

        world.setBlockToAir(pos);
    }*/

    @Override
    public final boolean hasTileEntity(BlockState state) {
        return info.hasTileEntity();
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return info.createTileEntity();
    }

    @Nullable
    public BlockDirection getDirection() {
        return null;
    }

    public final IBlockInfo getInfo() {
        return info;
    }

    protected boolean canAccessGui(BlockState state, World world, BlockPos pos, float hitX, float hitY, float hitZ) {
        for (CollisionGroup group : getCollisions(world.getTileEntity(pos), state)) {
            if (group.canAccessGui()) {
                for (AxisAlignedBB aabb : group.getItems()) {
                    if (CollisionUtils.isInBounds(aabb, hitX, hitY, hitZ)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            worldIn.updateComparatorOutputLevel(pos, this);

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    public List<CollisionGroup> getCollisions(TileEntity tile, BlockState state) {
        return DEFAULT_COLLISION_GROUPS;
    }

    /* TODO
    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(BlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
        for (CollisionGroup group : getCollisions(world.getTileEntity(pos), this.getActualState(state, world, pos))) {
            for (AxisAlignedBB aabb : group.getItems()) {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public RayTraceResult collisionRayTrace(BlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        AdvancedRayTraceResult result = AdvancedRayTracer.rayTrace(pos, start, end, getCollisions(world.getTileEntity(pos), this.getActualState(state, world, pos)));

        return result != null ? result.getHit() : null;
    }*/
}
