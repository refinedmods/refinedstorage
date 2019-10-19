package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.capability.NetworkNodeProxyCapability;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
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

            TileEntity placed = e.getWorld().getTileEntity(e.getPos());

            if (placed != null) {
                placed.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY).ifPresent(proxy -> {
                    discoverNode(e.getWorld(), e.getPos());

                    if (proxy.getNode() instanceof NetworkNode) {
                        ((NetworkNode) proxy.getNode()).setOwner(player.getGameProfile().getId());
                    }

                    for (Direction facing : Direction.values()) {
                        TileEntity side = e.getWorld().getTileEntity(e.getBlockSnapshot().getPos().offset(facing));

                        if (side != null) {
                            INetworkNodeProxy neighborProxy = side.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, facing.getOpposite()).orElse(null);

                            if (neighborProxy != null) {
                                INetworkNode node = neighborProxy.getNode();

                                if (node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, player)) {
                                    WorldUtils.sendNoPermissionMessage(player);

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

    private void discoverNode(IWorld world, BlockPos pos) {
        for (Direction facing : Direction.values()) {
            TileEntity tile = world.getTileEntity(pos.offset(facing));

            if (tile != null) {
                INetworkNodeProxy proxy = tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, facing.getOpposite()).orElse(null);
                if (proxy != null) {
                    INetworkNode node = proxy.getNode();

                    if (node.getNetwork() != null) {
                        node.getNetwork().getNodeGraph().invalidate(Action.PERFORM, node.getNetwork().getWorld(), node.getNetwork().getPosition());

                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (!e.getWorld().isRemote()) {
            TileEntity tile = e.getWorld().getTileEntity(e.getPos());

            if (tile != null) {
                tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY).ifPresent(nodeProxy -> {
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
