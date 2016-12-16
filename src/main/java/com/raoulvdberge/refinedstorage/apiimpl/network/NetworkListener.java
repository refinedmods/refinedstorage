package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.Permission;
import com.raoulvdberge.refinedstorage.proxy.CapabilityNetworkNode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NetworkListener {
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent e) {
        if (!e.getWorld().isRemote) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tile = e.getWorld().getTileEntity(e.getBlockSnapshot().getPos().offset(facing));

                if (tile != null && tile.hasCapability(CapabilityNetworkNode.NETWORK_NODE_CAPABILITY, facing.getOpposite())) {
                    INetworkNode node = tile.getCapability(CapabilityNetworkNode.NETWORK_NODE_CAPABILITY, facing.getOpposite());

                    if (node.getNetwork() != null && !node.getNetwork().hasPermission(Permission.MODIFY, e.getPlayer())) {
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

            if (tile != null && tile.hasCapability(CapabilityNetworkNode.NETWORK_NODE_CAPABILITY, null)) {

                INetworkNode node = tile.getCapability(CapabilityNetworkNode.NETWORK_NODE_CAPABILITY, null);

                if (node.getNetwork() != null && !node.getNetwork().hasPermission(Permission.MODIFY, e.getPlayer())) {
                    RSUtils.sendNoPermissionMessage(e.getPlayer());

                    e.setCanceled(true);

                    return;
                }
            }
        }
    }
}
