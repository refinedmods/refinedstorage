package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.ExternalStorageNetworkNode;
import com.refinedmods.refinedstorage.block.shape.ShapeCache;
import com.refinedmods.refinedstorage.container.ExternalStorageContainerMenu;
import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider;
import com.refinedmods.refinedstorage.render.ConstantsCable;
import com.refinedmods.refinedstorage.blockentity.ExternalStorageBlockEntity;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.CollisionUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class ExternalStorageBlock extends CableBlock {
    private static final VoxelShape HEAD_NORTH = Shapes.or(box(3, 3, 0, 13, 13, 2), HOLDER_NORTH);
    private static final VoxelShape HEAD_EAST = Shapes.or(box(14, 3, 3, 16, 13, 13), HOLDER_EAST);
    private static final VoxelShape HEAD_SOUTH = Shapes.or(box(3, 3, 14, 13, 13, 16), HOLDER_SOUTH);
    private static final VoxelShape HEAD_WEST = Shapes.or(box(0, 3, 3, 2, 13, 13), HOLDER_WEST);
    private static final VoxelShape HEAD_UP = Shapes.or(box(3, 14, 3, 13, 16, 13), HOLDER_UP);
    private static final VoxelShape HEAD_DOWN = Shapes.or(box(3, 0, 3, 13, 2, 13), HOLDER_DOWN);

    public ExternalStorageBlock() {
        super(BlockUtils.DEFAULT_GLASS_PROPERTIES);
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.ANY;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return ConstantsCable.addCoverVoxelShapes(ShapeCache.getOrCreate(state, s -> {
            VoxelShape shape = getCableShape(s);

            shape = Shapes.or(shape, getHeadShape(s));

            return shape;
        }), world, pos);
    }

    private VoxelShape getHeadShape(BlockState state) {
        Direction direction = state.getValue(getDirection().getProperty());

        if (direction == Direction.NORTH) {
            return HEAD_NORTH;
        }

        if (direction == Direction.EAST) {
            return HEAD_EAST;
        }

        if (direction == Direction.SOUTH) {
            return HEAD_SOUTH;
        }

        if (direction == Direction.WEST) {
            return HEAD_WEST;
        }

        if (direction == Direction.UP) {
            return HEAD_UP;
        }

        if (direction == Direction.DOWN) {
            return HEAD_DOWN;
        }

        return Shapes.empty();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExternalStorageBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && CollisionUtils.isInBounds(getHeadShape(state), pos, hit.getLocation())) {
            return NetworkUtils.attemptModify(level, pos, player, () -> NetworkHooks.openGui(
                (ServerPlayer) player,
                new BlockEntityMenuProvider<ExternalStorageBlockEntity>(
                    new TranslatableComponent("gui.refinedstorage.external_storage"),
                    (blockEntity, windowId, inventory, p) -> new ExternalStorageContainerMenu(blockEntity, player, windowId),
                    pos
                ),
                pos
            ));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        if (!level.isClientSide) {
            INetworkNode node = NetworkUtils.getNodeFromBlockEntity(level.getBlockEntity(pos));

            if (node instanceof ExternalStorageNetworkNode &&
                node.getNetwork() != null &&
                fromPos.equals(pos.relative(((ExternalStorageNetworkNode) node).getDirection()))) {
                ((ExternalStorageNetworkNode) node).updateStorage(node.getNetwork(), InvalidateCause.NEIGHBOR_CHANGED);
            }
        }
    }
}
