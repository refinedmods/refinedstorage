package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.container.DiskManipulatorContainer;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.tile.DiskManipulatorTile;
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

public class DiskManipulatorBlock extends ColoredNetworkBlock {
    public DiskManipulatorBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new DiskManipulatorTile();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult rayTraceResult) {
        ActionResultType result = BlockUtils.changeBlockColor(RSBlocks.DISK_MANIPULATOR, state, player.getHeldItem(handIn), world, pos, player);
        if (result != ActionResultType.PASS) {
            return result;
        }
        if (!world.isRemote) {
            return NetworkUtils.attemptModify(world, pos, rayTraceResult.getFace(), player, () -> NetworkHooks.openGui(
                (ServerPlayerEntity) player,
                new PositionalTileContainerProvider<DiskManipulatorTile>(
                    new TranslationTextComponent("gui.refinedstorage.disk_manipulator"),
                    (tile, windowId, inventory, p) -> new DiskManipulatorContainer(tile, p, windowId),
                    pos
                ),
                pos
            ));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
