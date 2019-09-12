package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NetworkNodeListener {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent e) {
        if (!e.world.isRemote()) {
            if (e.phase == TickEvent.Phase.END) {
                e.world.getProfiler().startSection("network node ticking");

                for (INetworkNode node : API.instance().getNetworkNodeManager(e.world).all()) {
                    node.update();
                }

                e.world.getProfiler().endSection();
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
        if (!e.getWorld().isRemote() && e.getEntity() instanceof PlayerEntity) {
            TileEntity placed = e.getWorld().getTileEntity(e.getPos());

            if (placed != null) {
                placed.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY).ifPresent(x -> {
                    for (Direction facing : Direction.values()) {
                        TileEntity side = e.getWorld().getTileEntity(e.getBlockSnapshot().getPos().offset(facing));

                        if (side != null) {
                            INetworkNodeProxy nodeProxy = side.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing.getOpposite()).orElse(null);

                            if (nodeProxy != null) {
                                INetworkNode node = nodeProxy.getNode();

                                if (node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, (PlayerEntity) e.getEntity())) {
                                    WorldUtils.sendNoPermissionMessage((PlayerEntity) e.getEntity());

                                    e.setCanceled(true);

                                    return;
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (!e.getWorld().isRemote()) {
            TileEntity tile = e.getWorld().getTileEntity(e.getPos());

            if (tile != null) {
                tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY).ifPresent(nodeProxy -> {
                    INetworkNode node = nodeProxy.getNode();

                    if (node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, e.getPlayer())) {
                        WorldUtils.sendNoPermissionMessage(e.getPlayer());

                        e.setCanceled(true);
                    }
                });
            }
        }
    }
}
