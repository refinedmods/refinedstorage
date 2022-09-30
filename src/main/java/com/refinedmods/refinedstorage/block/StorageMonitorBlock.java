package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.apiimpl.network.node.StorageMonitorNetworkNode;
import com.refinedmods.refinedstorage.blockentity.StorageMonitorBlockEntity;
import com.refinedmods.refinedstorage.container.StorageMonitorContainerMenu;
import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class StorageMonitorBlock extends NetworkNodeBlock {
    public StorageMonitorBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StorageMonitorBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!level.isClientSide) {
            ItemStack held = player.getItemInHand(handIn);

            if (player.isCrouching()) {
                return NetworkUtils.attemptModify(level, pos, player, () -> NetworkHooks.openScreen(
                    (ServerPlayer) player,
                    new BlockEntityMenuProvider<StorageMonitorBlockEntity>(
                        Component.translatable("gui.refinedstorage.storage_monitor"),
                        (blockEntity, windowId, inventory, p) -> new StorageMonitorContainerMenu(blockEntity, player, windowId),
                        pos
                    ),
                    pos
                ));
            } else {
                StorageMonitorNetworkNode storageMonitor = ((StorageMonitorBlockEntity) level.getBlockEntity(pos)).getNode();

                if (!held.isEmpty()) {
                    return storageMonitor.deposit(player, held);
                } else {
                    return storageMonitor.depositAll(player);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);

        if (!level.isClientSide) {
            HitResult result = LevelUtils.rayTracePlayer(level, player);

            if (!(result instanceof BlockHitResult)) {
                return;
            }

            ((StorageMonitorBlockEntity) level.getBlockEntity(pos)).getNode().extract(player, ((BlockHitResult) result).getDirection());
        }
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
