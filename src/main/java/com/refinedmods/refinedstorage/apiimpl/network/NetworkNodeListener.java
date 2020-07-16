package com.refinedmods.refinedstorage.apiimpl.network;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NetworkNodeListener {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        if (!e.world.isRemote()) {
            if (e.phase == TickEvent.Phase.END) {
                e.world.getProfiler().startSection("network node ticking");
                for (INetwork network : API.instance().getNetworkManager((ServerWorld) e.world).all()) {
                    network.checkLoaded();
                }
                for (INetworkNode node : API.instance().getNetworkNodeManager((ServerWorld) e.world).all()) {
                    node.update();
                }
                e.world.getProfiler().endSection();
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
        if (!e.getWorld().isRemote() && e.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) e.getEntity();

            INetworkNode placed = NetworkUtils.getNodeFromTile(e.getWorld().getTileEntity(e.getPos()));

            if (placed != null) {
                discoverNode(e.getWorld(), e.getPos());

                placed.setOwner(player.getGameProfile().getId());

                for (Direction facing : Direction.values()) {
                    INetworkNode node = NetworkUtils.getNodeFromTile(e.getWorld().getTileEntity(e.getBlockSnapshot().getPos().offset(facing)));

                    if (node != null && node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, player)) {
                        WorldUtils.sendNoPermissionMessage(player);

                        e.setCanceled(true);

                        return;
                    }
                }
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
