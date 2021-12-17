package com.refinedmods.refinedstorage.apiimpl.network;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.PlayerUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NetworkNodeListener {
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
        if (e.getWorld().isClientSide() || !(e.getEntity() instanceof Player player)) {
            return;
        }

        INetworkNode placed = NetworkUtils.getNodeAtPosition((Level) e.getWorld(), e.getPos());
        if (placed == null) {
            return;
        }

        if (willJoinNetworkWithoutPermission((Level) e.getWorld(), e.getPos(), player)) {
            cancelBlockPlacement(e, player);
            return;
        }

        updateOwner(player, placed);
    }

    private boolean willJoinNetworkWithoutPermission(Level level, BlockPos origin, Player player) {
        for (Direction facing : Direction.values()) {
            INetworkNode node = NetworkUtils.getNodeAtPosition(level, origin.relative(facing));
            if (node != null && node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, player)) {
                return true;
            }
        }
        return false;
    }

    private void cancelBlockPlacement(BlockEvent.EntityPlaceEvent e, Player player) {
        WorldUtils.sendNoPermissionMessage(player);
        e.setCanceled(true);
        // Fixes desync as we do not cancel the event clientside
        PlayerUtils.updateHeldItems((ServerPlayer) player);
    }

    private void updateOwner(Player player, INetworkNode placed) {
        placed.setOwner(player.getGameProfile().getId());
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (!e.getWorld().isClientSide()) {
            INetworkNode node = NetworkUtils.getNodeAtPosition((Level) e.getWorld(), e.getPos());
            if (node != null && node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, e.getPlayer())) {
                WorldUtils.sendNoPermissionMessage(e.getPlayer());
                e.setCanceled(true);
            }
        }
    }
}
