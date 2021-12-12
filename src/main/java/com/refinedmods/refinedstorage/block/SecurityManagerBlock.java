package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.container.SecurityManagerContainer;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.tile.SecurityManagerTile;
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
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ActionResultType result = RSBlocks.SECURITY_MANAGER.changeBlockColor(state, player.getItemInHand(hand), world, pos, player);
        if (result != ActionResultType.PASS) {
            return result;
        }

        if (!world.isClientSide) {
            Runnable action = () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new PositionalTileContainerProvider<SecurityManagerTile>(
                    new TranslationTextComponent("gui.refinedstorage.security_manager"),
                    (tile, windowId, inventory, p) -> new SecurityManagerContainer(tile, player, windowId),
                    pos
                ),
                pos
            );

            if (player.getGameProfile().getId().equals(((SecurityManagerTile) world.getBlockEntity(pos)).getNode().getOwner())) {
                action.run();
            } else {
                return NetworkUtils.attempt(world, pos, player, action, Permission.MODIFY, Permission.SECURITY);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SecurityManagerTile();
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
