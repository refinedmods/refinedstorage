package com.raoulvdberge.refinedstorage.util;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.capability.NetworkNodeProxyCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetworkUtils {
    public static boolean attemptModify(World world, BlockPos pos, Direction facing, PlayerEntity player, Runnable action) {
        return attempt(world, pos, facing, player, action, Permission.MODIFY);
    }

    public static boolean attempt(World world, BlockPos pos, Direction facing, PlayerEntity player, Runnable action, Permission... permissionsRequired) {
        if (world.isRemote) {
            return true;
        }

        TileEntity tile = world.getTileEntity(pos);

        if (tile != null) {
            INetworkNodeProxy proxy = tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, facing).orElse(null);

            if (proxy != null) {
                INetworkNode node = proxy.getNode();

                if (node.getNetwork() != null) {
                    for (Permission permission : permissionsRequired) {
                        if (!node.getNetwork().getSecurityManager().hasPermission(permission, player)) {
                            WorldUtils.sendNoPermissionMessage(player);

                            return true; // Avoid placing blocks
                        }
                    }
                }
            }
        }

        action.run();

        return true;
    }
}
