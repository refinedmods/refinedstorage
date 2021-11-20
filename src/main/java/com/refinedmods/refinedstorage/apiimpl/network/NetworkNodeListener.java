package com.refinedmods.refinedstorage.apiimpl.network;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.PlayerUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NetworkNodeListener {
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
        if (!e.getWorld().isRemote() && e.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) e.getEntity();

            INetworkNode placed = NetworkUtils.getNodeFromTile(e.getWorld().getTileEntity(e.getPos()));

            if (placed != null) {
                for (Direction facing : Direction.values()) {
                    INetworkNode node = NetworkUtils.getNodeFromTile(e.getWorld().getTileEntity(e.getBlockSnapshot().getPos().offset(facing)));

                    if (node != null && node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, player)) {
                        WorldUtils.sendNoPermissionMessage(player);

                        e.setCanceled(true);

                        //Fixes desync as we do not cancel the event clientside
                        PlayerUtils.updateHeldItems((ServerPlayerEntity) player);

                        return;
                    }
                }

                discoverNode(e.getWorld(), e.getPos());

                placed.setOwner(player.getGameProfile().getId());
            }
        }
    }

    private void discoverNode(IWorld world, BlockPos pos) {
        for (Direction facing : Direction.values()) {
            INetworkNode node = NetworkUtils.getNodeFromTile(world.getTileEntity(pos.offset(facing)));

            if (node != null && node.getNetwork() != null) {
                node.getNetwork().getNodeGraph().invalidate(Action.PERFORM, node.getNetwork().getWorld(), node.getNetwork().getPosition());

                return;
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (!e.getWorld().isRemote()) {
            INetworkNode node = NetworkUtils.getNodeFromTile(e.getWorld().getTileEntity(e.getPos()));

            if (node != null && node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, e.getPlayer())) {
                WorldUtils.sendNoPermissionMessage(e.getPlayer());

                e.setCanceled(true);
            }
        }
    }
}
