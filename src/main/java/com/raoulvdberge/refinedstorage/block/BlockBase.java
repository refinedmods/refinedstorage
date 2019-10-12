package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;
import com.raoulvdberge.refinedstorage.item.blockitem.ItemBlockBase;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.collision.CollisionGroup;
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

    protected IBlockInfo info;

    public BlockBase(IBlockInfo info) {
        super(Block.Properties
            .create(info.getMaterial())
            .hardnessAndResistance(info.getHardness())
            .sound(info.getSoundType())
        );

        this.info = info;

        setRegistryName(info.getId());
    }

    public BlockBase(Properties p_i48440_1_) {
        super(p_i48440_1_);
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
        /*for (CollisionGroup group : getCollisions(world.getTileEntity(pos), state)) {
            if (group.canAccessGui()) {
                for (AxisAlignedBB aabb : group.getItems()) {
                    if (CollisionUtils.isInBounds(aabb, hitX, hitY, hitZ)) {
                        return true;
                    }
                }
            }
        }*/

        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            worldIn.updateComparatorOutputLevel(pos, this);

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }
}
