package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.apiimpl.network.node.StorageMonitorNetworkNode;
import com.refinedmods.refinedstorage.container.StorageMonitorContainer;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.tile.StorageMonitorTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class StorageMonitorBlock extends NetworkNodeBlock {
    public StorageMonitorBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new StorageMonitorTile();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            ItemStack held = player.inventory.getSelected();

            if (player.isCrouching()) {
                return NetworkUtils.attemptModify(world, pos, player, () -> NetworkHooks.openGui(
                    (ServerPlayerEntity) player,
                    new PositionalTileContainerProvider<StorageMonitorTile>(
                        new TranslationTextComponent("gui.refinedstorage.storage_monitor"),
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

        return ActionResultType.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void attack(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        super.attack(state, world, pos, player);

        if (!world.isClientSide) {
            RayTraceResult result = WorldUtils.rayTracePlayer(world, player);

            if (!(result instanceof BlockRayTraceResult)) {
                return;
            }

            ((StorageMonitorTile) world.getBlockEntity(pos)).getNode().extract(player, ((BlockRayTraceResult) result).getDirection());
        }
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
