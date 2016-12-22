package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.WorldSavedDataNetworkNode;
import com.raoulvdberge.refinedstorage.proxy.CapabilityNetworkNodeProxy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NetworkNodeListener {
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent e) {
        if (!e.getWorld().isRemote) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tile = e.getWorld().getTileEntity(e.getBlockSnapshot().getPos().offset(facing));

                if (tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing.getOpposite())) {
                    INetworkNodeProxy nodeProxy = tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, facing.getOpposite());
                    INetworkNode node = nodeProxy.getNode();

                    if (node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, e.getPlayer())) {
                        RSUtils.sendNoPermissionMessage(e.getPlayer());

                        e.setCanceled(true);

                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (!e.getWorld().isRemote) {
            TileEntity tile = e.getWorld().getTileEntity(e.getPos());

            if (tile != null && tile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, null)) {
                INetworkNodeProxy nodeProxy = tile.getCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, null);
                INetworkNode node = nodeProxy.getNode();

                if (node.getNetwork() != null && !node.getNetwork().getSecurityManager().hasPermission(Permission.BUILD, e.getPlayer())) {
                    RSUtils.sendNoPermissionMessage(e.getPlayer());

                    e.setCanceled(true);

                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save e) {
        WorldSavedDataNetworkNode.get(e.getWorld());
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        WorldSavedDataNetworkNode.get(e.getWorld());
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        API.instance().getNetworkNodeProvider(e.getWorld().provider.getDimension()).clear();
    }
}
