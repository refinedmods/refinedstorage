package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.apiimpl.network.node.StorageMonitorNetworkNode;
import com.refinedmods.refinedstorage.container.StorageMonitorContainer;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.tile.CrafterTile;
import com.refinedmods.refinedstorage.tile.StorageMonitorTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

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
        return new StorageMonitorTile(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!world.isClientSide) {
            ItemStack held = player.containerMenu.getCarried();

            if (player.isCrouching()) {
                return NetworkUtils.attemptModify(world, pos, player, () -> NetworkHooks.openGui(
                    (ServerPlayer) player,
                    new PositionalTileContainerProvider<StorageMonitorTile>(
                        new TranslatableComponent("gui.refinedstorage.storage_monitor"),
                        (tile, windowId, inventory, p) -> new StorageMonitorContainer(tile, player, windowId),
                        pos
                    ),
                    pos
                ));
            } else {
                StorageMonitorNetworkNode storageMonitor = ((StorageMonitorTile) world.getBlockEntity(pos)).getNode();

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
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        super.attack(state, world, pos, player);

        if (!world.isClientSide) {
            HitResult result = WorldUtils.rayTracePlayer(world, player);

            if (!(result instanceof BlockHitResult)) {
                return;
            }

            ((StorageMonitorTile) world.getBlockEntity(pos)).getNode().extract(player, ((BlockHitResult) result).getDirection());
        }
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
