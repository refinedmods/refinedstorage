package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.container.NetworkTransmitterContainer;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.tile.NetworkTransmitterTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class NetworkTransmitterBlock extends ColoredNetworkBlock {
    public NetworkTransmitterBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new NetworkTransmitterTile();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ActionResultType result = RSBlocks.NETWORK_TRANSMITTER.changeBlockColor(state, player.getItemInHand(hand), world, pos, player);
        if (result != ActionResultType.PASS) {
            return result;
        }

        if (!world.isClientSide) {
            return NetworkUtils.attemptModify(world, pos, player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new PositionalTileContainerProvider<NetworkTransmitterTile>(
                    new TranslationTextComponent("gui.refinedstorage.network_transmitter"),
                    (tile, windowId, inventory, p) -> new NetworkTransmitterContainer(tile, player, windowId),
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
