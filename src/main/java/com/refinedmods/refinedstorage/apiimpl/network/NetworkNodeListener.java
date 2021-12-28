package com.refinedmods.refinedstorage.apiimpl.network;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.PlayerUtils;
import com.refinedmods.refinedstorage.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NetworkNodeListener {
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
        if (!e.getWorld().isClientSide() && e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();

            INetworkNode placed = NetworkUtils.getNodeFromBlockEntity(e.getWorld().getBlockEntity(e.getPos()));

            if (placed != null) {
                for (Direction facing : Direction.values()) {
                    INetworkNode node = NetworkUtils.getNodeFromBlockEntity(e.getWorld().getBlockEntity(e.getBlockSnapshot().getPos().relative(facing)));

                    if (node != null && node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, player)) {
                        LevelUtils.sendNoPermissionMessage(player);

                        e.setCanceled(true);

                        //Fixes desync as we do not cancel the event clientside
                        PlayerUtils.updateHeldItems((ServerPlayer) player);

                        return;
                    }
                }

                discoverNode(e.getWorld(), e.getPos());

                placed.setOwner(player.getGameProfile().getId());
            }
        }
    }

    private void discoverNode(LevelAccessor world, BlockPos pos) {
        for (Direction facing : Direction.values()) {
            INetworkNode node = NetworkUtils.getNodeFromBlockEntity(world.getBlockEntity(pos.relative(facing)));

            if (node != null && node.getNetwork() != null) {
                node.getNetwork().getNodeGraph().invalidate(Action.PERFORM, node.getNetwork().getLevel(), node.getNetwork().getPosition());

                return;
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (!e.getWorld().isClientSide()) {
            INetworkNode node = NetworkUtils.getNodeFromBlockEntity(e.getWorld().getBlockEntity(e.getPos()));

            if (node != null && node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, e.getPlayer())) {
                LevelUtils.sendNoPermissionMessage(e.getPlayer());

                e.setCanceled(true);
            }
        }
    }
}
