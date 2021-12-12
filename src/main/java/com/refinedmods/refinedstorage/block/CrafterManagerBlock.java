package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.container.factory.CrafterManagerContainerProvider;
import com.refinedmods.refinedstorage.tile.CrafterManagerTile;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class CrafterManagerBlock extends ColoredNetworkBlock {
    public CrafterManagerBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CrafterManagerTile();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ActionResultType result = RSBlocks.CRAFTER_MANAGER.changeBlockColor(state, player.getItemInHand(hand), world, pos, player);
        if (result != ActionResultType.PASS) {
            return result;
        }

        if (!world.isClientSide) {
            return NetworkUtils.attempt(world, pos, player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new CrafterManagerContainerProvider((CrafterManagerTile) world.getBlockEntity(pos)),
                buf -> CrafterManagerContainerProvider.writeToBuffer(buf, world, pos)
            ), Permission.MODIFY, Permission.AUTOCRAFTING);
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
