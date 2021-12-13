package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.container.DetectorContainer;
import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider;
import com.refinedmods.refinedstorage.blockentity.DetectorBlockEntity;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.ColorMap;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class DetectorBlock extends ColoredNetworkBlock {
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 5, 16);

    public DetectorBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.registerDefaultState(this.getStateDefinition().any().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(POWERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        return (blockEntity instanceof DetectorBlockEntity && ((DetectorBlockEntity) blockEntity).getNode().isPowered()) ? 15 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ColorMap<DetectorBlock> colorMap = RSBlocks.DETECTOR;
        DyeColor color = DyeColor.getColor(player.getItemInHand(hand));

        if (color != null && !state.getBlock().equals(colorMap.get(color).get())) {
            BlockState newState = colorMap.get(color).get().defaultBlockState().setValue(POWERED, state.getValue(POWERED));

            return RSBlocks.DETECTOR.setBlockState(newState, player.getItemInHand(hand), level, pos, player);
        }

        if (!level.isClientSide) {
            return NetworkUtils.attemptModify(level, pos, player, () -> NetworkHooks.openGui(
                (ServerPlayer) player,
                new BlockEntityMenuProvider<DetectorBlockEntity>(
                    new TranslatableComponent("gui.refinedstorage.detector"),
                    (blockEntity, windowId, inventory, p) -> new DetectorContainer(blockEntity, player, windowId),
                    pos
                ),
                pos
            ));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DetectorBlockEntity(pos, state);
    }
}
