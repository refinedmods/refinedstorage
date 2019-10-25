package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.StorageMonitorNetworkNode;
import com.raoulvdberge.refinedstorage.container.StorageMonitorContainer;
import com.raoulvdberge.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.raoulvdberge.refinedstorage.tile.StorageMonitorTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import com.raoulvdberge.refinedstorage.util.NetworkUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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

        this.setRegistryName(RS.ID, "storage_monitor");
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
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isRemote) {
            ItemStack held = player.inventory.getCurrentItem();

            if (player.isSneaking()) {
                return NetworkUtils.attemptModify(world, pos, hit.getFace(), player, () -> NetworkHooks.openGui(
                    (ServerPlayerEntity) player,
                    new PositionalTileContainerProvider<StorageMonitorTile>(
                        new TranslationTextComponent("gui.refinedstorage.storage_monitor"),
                        (tile, windowId, inventory, p) -> new StorageMonitorContainer(tile, player, windowId),
                        pos
                    ),
                    pos
                ));
            } else {
                StorageMonitorNetworkNode storageMonitor = ((StorageMonitorTile) world.getTileEntity(pos)).getNode();

                if (!held.isEmpty()) {
                    return storageMonitor.deposit(player, held);
                } else {
                    return storageMonitor.depositAll(player);
                }
            }
        }

        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        super.onBlockClicked(state, world, pos, player);

        if (!world.isRemote) {
            RayTraceResult result = WorldUtils.rayTracePlayer(world, player);

            if (!(result instanceof BlockRayTraceResult)) {
                return;
            }

            ((StorageMonitorTile) world.getTileEntity(pos)).getNode().extract(player, ((BlockRayTraceResult) result).getFace());
        }
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
