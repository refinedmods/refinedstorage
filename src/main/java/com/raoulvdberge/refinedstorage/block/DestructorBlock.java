package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.DestructorContainer;
import com.raoulvdberge.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.raoulvdberge.refinedstorage.tile.DestructorTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class DestructorBlock extends CableBlock {
    private static final VoxelShape HEAD_NORTH = VoxelShapes.or(makeCuboidShape(2, 2, 0, 14, 14, 2), HOLDER_NORTH);
    private static final VoxelShape HEAD_EAST = VoxelShapes.or(makeCuboidShape(14, 2, 2, 16, 14, 14), HOLDER_EAST);
    private static final VoxelShape HEAD_SOUTH = VoxelShapes.or(makeCuboidShape(2, 2, 14, 14, 14, 16), HOLDER_SOUTH);
    private static final VoxelShape HEAD_WEST = VoxelShapes.or(makeCuboidShape(0, 2, 2, 2, 14, 14), HOLDER_WEST);
    private static final VoxelShape HEAD_DOWN = VoxelShapes.or(makeCuboidShape(2, 0, 2, 14, 2, 14), HOLDER_DOWN);
    private static final VoxelShape HEAD_UP = VoxelShapes.or(makeCuboidShape(2, 14, 2, 14, 16, 14), HOLDER_UP);

    public DestructorBlock() {
        super(BlockUtils.DEFAULT_GLASS_PROPERTIES);

        this.setRegistryName(RS.ID, "destructor");
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.ANY;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new DestructorTile();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        VoxelShape shape = super.getShape(state, world, pos, ctx);

        Direction direction = state.get(getDirection().getProperty());

        if (direction == Direction.NORTH) {
            shape = VoxelShapes.or(shape, HEAD_NORTH);
        }

        if (direction == Direction.EAST) {
            shape = VoxelShapes.or(shape, HEAD_EAST);
        }

        if (direction == Direction.SOUTH) {
            shape = VoxelShapes.or(shape, HEAD_SOUTH);
        }

        if (direction == Direction.WEST) {
            shape = VoxelShapes.or(shape, HEAD_WEST);
        }

        if (direction == Direction.UP) {
            shape = VoxelShapes.or(shape, HEAD_UP);
        }

        if (direction == Direction.DOWN) {
            shape = VoxelShapes.or(shape, HEAD_DOWN);
        }

        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            return NetworkUtils.attemptModify(world, pos, hit.getFace(), player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new PositionalTileContainerProvider<DestructorTile>(
                    new TranslationTextComponent("gui.refinedstorage.destructor"),
                    (tile, windowId, inventory, p) -> new DestructorContainer(tile, player, windowId),
                    pos
                ),
                pos
            ));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
