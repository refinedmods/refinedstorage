package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.block.shape.ShapeCache;
import com.refinedmods.refinedstorage.container.ImporterContainer;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.render.ConstantsCable;
import com.refinedmods.refinedstorage.tile.ImporterTile;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class ImporterBlock extends CableBlock {
    private static final VoxelShape LINE_NORTH_1 = box(6, 6, 4, 10, 10, 6);
    private static final VoxelShape LINE_NORTH_2 = box(5, 5, 2, 11, 11, 4);
    private static final VoxelShape LINE_NORTH_3 = box(3, 3, 0, 13, 13, 2);
    private static final VoxelShape LINE_NORTH = Shapes.or(LINE_NORTH_1, LINE_NORTH_2, LINE_NORTH_3);

    private static final VoxelShape LINE_EAST_1 = box(10, 6, 6, 12, 10, 10);
    private static final VoxelShape LINE_EAST_2 = box(12, 5, 5, 14, 11, 11);
    private static final VoxelShape LINE_EAST_3 = box(14, 3, 3, 16, 13, 13);
    private static final VoxelShape LINE_EAST = Shapes.or(LINE_EAST_1, LINE_EAST_2, LINE_EAST_3);

    private static final VoxelShape LINE_SOUTH_1 = box(6, 6, 10, 10, 10, 12);
    private static final VoxelShape LINE_SOUTH_2 = box(5, 5, 12, 11, 11, 14);
    private static final VoxelShape LINE_SOUTH_3 = box(3, 3, 14, 13, 13, 16);
    private static final VoxelShape LINE_SOUTH = Shapes.or(LINE_SOUTH_1, LINE_SOUTH_2, LINE_SOUTH_3);

    private static final VoxelShape LINE_WEST_1 = box(4, 6, 6, 6, 10, 10);
    private static final VoxelShape LINE_WEST_2 = box(2, 5, 5, 4, 11, 11);
    private static final VoxelShape LINE_WEST_3 = box(0, 3, 3, 2, 13, 13);
    private static final VoxelShape LINE_WEST = Shapes.or(LINE_WEST_1, LINE_WEST_2, LINE_WEST_3);

    private static final VoxelShape LINE_UP_1 = box(6, 10, 6, 10, 12, 10);
    private static final VoxelShape LINE_UP_2 = box(5, 12, 5, 11, 14, 11);
    private static final VoxelShape LINE_UP_3 = box(3, 14, 3, 13, 16, 13);
    private static final VoxelShape LINE_UP = Shapes.or(LINE_UP_1, LINE_UP_2, LINE_UP_3);

    private static final VoxelShape LINE_DOWN_1 = box(6, 4, 6, 10, 6, 10);
    private static final VoxelShape LINE_DOWN_2 = box(5, 2, 5, 11, 4, 11);
    private static final VoxelShape LINE_DOWN_3 = box(3, 0, 3, 13, 2, 13);
    private static final VoxelShape LINE_DOWN = Shapes.or(LINE_DOWN_1, LINE_DOWN_2, LINE_DOWN_3);

    public ImporterBlock() {
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

            shape = Shapes.or(shape, getLineShape(s));

            return shape;
        }), world, pos);
    }

    private VoxelShape getLineShape(BlockState state) {
        Direction direction = state.getValue(getDirection().getProperty());

        if (direction == Direction.NORTH) {
            return LINE_NORTH;
        }

        if (direction == Direction.EAST) {
            return LINE_EAST;
        }

        if (direction == Direction.SOUTH) {
            return LINE_SOUTH;
        }

        if (direction == Direction.WEST) {
            return LINE_WEST;
        }

        if (direction == Direction.UP) {
            return LINE_UP;
        }

        if (direction == Direction.DOWN) {
            return LINE_DOWN;
        }

        return Shapes.empty();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ImporterTile(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && CollisionUtils.isInBounds(getLineShape(state), pos, hit.getLocation())) {
            return NetworkUtils.attemptModify(level, pos, player, () -> NetworkHooks.openGui(
                (ServerPlayer) player,
                new PositionalTileContainerProvider<ImporterTile>(
                    new TranslatableComponent("gui.refinedstorage.importer"),
                    (tile, windowId, inventory, p) -> new ImporterContainer(tile, player, windowId),
                    pos
                ),
                pos
            ));
        }

        return InteractionResult.SUCCESS;
    }
}
