package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.container.SecurityManagerContainer;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.tile.SecurityManagerTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class SecurityManagerBlock extends ColoredNetworkBlock {
    public SecurityManagerBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = RSBlocks.SECURITY_MANAGER.changeBlockColor(state, player.getItemInHand(hand), level, pos, player);
        if (result != InteractionResult.PASS) {
            return result;
        }

        if (!level.isClientSide) {
            Runnable action = () -> NetworkHooks.openGui(
                (ServerPlayer) player,
                new PositionalTileContainerProvider<SecurityManagerTile>(
                    new TranslatableComponent("gui.refinedstorage.security_manager"),
                    (tile, windowId, inventory, p) -> new SecurityManagerContainer(tile, player, windowId),
                    pos
                ),
                pos
            );

            if (player.getGameProfile().getId().equals(((SecurityManagerTile) level.getBlockEntity(pos)).getNode().getOwner())) {
                action.run();
            } else {
                return NetworkUtils.attempt(level, pos, player, action, Permission.MODIFY, Permission.SECURITY);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SecurityManagerTile(pos, state);
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
