package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.container.FluidInterfaceContainerMenu;
import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider;
import com.refinedmods.refinedstorage.blockentity.FluidInterfaceBlockEntity;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class FluidInterfaceBlock extends NetworkNodeBlock {
    public FluidInterfaceBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidInterfaceBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!level.isClientSide) {
            return NetworkUtils.attempt(level, pos, player, () -> player.openMenu(
                new BlockEntityMenuProvider<FluidInterfaceBlockEntity>(
                    Component.translatable("gui.refinedstorage.fluid_interface"),
                    (blockEntity, windowId, inventory, p) -> new FluidInterfaceContainerMenu(blockEntity, player, windowId),
                    pos
                ),
                pos
            ), Permission.MODIFY, Permission.INSERT, Permission.EXTRACT);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
