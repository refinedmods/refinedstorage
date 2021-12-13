package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.container.NetworkTransmitterContainer;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.tile.NetworkTransmitterTile;
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

public class NetworkTransmitterBlock extends ColoredNetworkBlock {
    public NetworkTransmitterBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NetworkTransmitterTile(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = RSBlocks.NETWORK_TRANSMITTER.changeBlockColor(state, player.getItemInHand(hand), level, pos, player);
        if (result != InteractionResult.PASS) {
            return result;
        }

        if (!level.isClientSide) {
            return NetworkUtils.attemptModify(level, pos, player, () -> NetworkHooks.openGui(
                (ServerPlayer) player,
                new PositionalTileContainerProvider<NetworkTransmitterTile>(
                    new TranslatableComponent("gui.refinedstorage.network_transmitter"),
                    (tile, windowId, inventory, p) -> new NetworkTransmitterContainer(tile, player, windowId),
                    pos
                ),
                pos
            ));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
