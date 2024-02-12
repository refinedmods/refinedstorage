package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.GridBlockGridFactory;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import com.refinedmods.refinedstorage.util.BlockColorMap;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.ColorMap;
import com.refinedmods.refinedstorage.util.NetworkUtils;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GridBlock extends ColoredNetworkBlock {
    private final GridType type;

    public GridBlock(GridType type) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);

        this.type = type;
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GridBlockEntity(type, pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockColorMap<?> map;
        switch (type) {
            case FLUID:
                map = RSBlocks.FLUID_GRID;
                break;
            case NORMAL:
                map = RSBlocks.GRID;
                break;
            case CRAFTING:
                map = RSBlocks.CRAFTING_GRID;
                break;
            case PATTERN:
                map = RSBlocks.PATTERN_GRID;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        InteractionResult result = map.changeBlockColor(state, player.getItemInHand(hand), level, pos, player);
        if (result != InteractionResult.PASS) {
            return result;
        }

        if (!level.isClientSide) {
            return NetworkUtils.attemptModify(level, pos, player, () -> API.instance().getGridManager().openGrid(GridBlockGridFactory.ID, (ServerPlayer) player, pos));
        }

        return InteractionResult.SUCCESS;
    }
}
