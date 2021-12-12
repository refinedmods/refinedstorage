package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class NetworkUtils {
    private NetworkUtils() {
    }

    @Nullable
    public static INetworkNode getNodeFromTile(@Nullable TileEntity tile) {
        if (tile != null) {
            INetworkNodeProxy<?> proxy = tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY).orElse(null);
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

    public static ActionResultType attemptModify(World world, BlockPos pos, PlayerEntity player, Runnable action) {
        return attempt(world, pos, player, action, Permission.MODIFY);
    }

    public static ActionResultType attempt(World world, BlockPos pos, PlayerEntity player, Runnable action, Permission... permissionsRequired) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }

        INetwork network = getNetworkFromNode(getNodeFromTile(world.getBlockEntity(pos)));

        if (network != null) {
            for (Permission permission : permissionsRequired) {
                if (!network.getSecurityManager().hasPermission(permission, player)) {
                    WorldUtils.sendNoPermissionMessage(player);

                    return ActionResultType.SUCCESS;
                }
            }
        }

        action.run();

        return ActionResultType.SUCCESS;
    }

    public static void extractBucketFromPlayerInventoryOrNetwork(PlayerEntity player, INetwork network, Consumer<ItemStack> onBucketFound) {
        for (int i = 0; i < player.inventory.getContainerSize(); ++i) {
            ItemStack slot = player.inventory.getItem(i);

            if (API.instance().getComparer().isEqualNoQuantity(StackUtils.EMPTY_BUCKET, slot)) {
                player.inventory.removeItem(i, 1);

                onBucketFound.accept(StackUtils.EMPTY_BUCKET.copy());

                return;
            }
        }

        ItemStack fromNetwork = network.extractItem(StackUtils.EMPTY_BUCKET, 1, Action.PERFORM);
        if (!fromNetwork.isEmpty()) {
            onBucketFound.accept(fromNetwork);
        }
    }
}
