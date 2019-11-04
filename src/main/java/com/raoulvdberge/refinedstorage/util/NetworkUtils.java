package com.raoulvdberge.refinedstorage.util;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.capability.NetworkNodeProxyCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class NetworkUtils {
    @Nullable
    public static INetworkNode getNodeFromTile(@Nullable TileEntity tile) {
        if (tile != null) {
            INetworkNodeProxy proxy = tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY).orElse(null);

            if (proxy != null) {
                return proxy.getNode();
            }
        }

        return null;
    }

    @Nullable
    public static INetwork getNetworkFromNode(@Nullable INetworkNode node) {
        if (node != null) {
            return node.getNetwork();
        }

        return null;
    }

    public static boolean attemptModify(World world, BlockPos pos, Direction facing, PlayerEntity player, Runnable action) {
        return attempt(world, pos, facing, player, action, Permission.MODIFY);
    }

    public static boolean attempt(World world, BlockPos pos, Direction facing, PlayerEntity player, Runnable action, Permission... permissionsRequired) {
        if (world.isRemote) {
            return true;
        }

        INetwork network = getNetworkFromNode(getNodeFromTile(world.getTileEntity(pos)));

        if (network != null) {
            for (Permission permission : permissionsRequired) {
                if (!network.getSecurityManager().hasPermission(permission, player)) {
                    WorldUtils.sendNoPermissionMessage(player);

                    return true;
                }
            }
        }

        action.run();

        return true;
    }
}
