package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.container.CrafterContainer;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.tile.CrafterTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class CrafterBlock extends ColoredNetworkBlock {
    public CrafterBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.ANY_FACE_PLAYER;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CrafterTile();
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);

        if (!world.isClientSide) {
            TileEntity tile = world.getBlockEntity(pos);

            if (tile instanceof CrafterTile && stack.hasCustomHoverName()) {
                ((CrafterTile) tile).getNode().setDisplayName(stack.getHoverName());
                ((CrafterTile) tile).getNode().markDirty();
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ActionResultType result = RSBlocks.CRAFTER.changeBlockColor(state, player.getItemInHand(hand), world, pos, player);
        if (result != ActionResultType.PASS) {
            return result;
        }

        if (!world.isClientSide) {
            return NetworkUtils.attempt(world, pos, player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new PositionalTileContainerProvider<CrafterTile>(
                    ((CrafterTile) world.getBlockEntity(pos)).getNode().getName(),
                    (tile, windowId, inventory, p) -> new CrafterContainer(tile, player, windowId),
                    pos
                ),
                pos
            ), Permission.MODIFY, Permission.AUTOCRAFTING);
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
