package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class NetworkUtils {
    private NetworkUtils() {
    }

    @Nullable
    public static INetworkNode getNodeAtPosition(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return null;
        }
        return getNodeFromBlockEntity(level.getBlockEntity(pos));
    }

    @Nullable
    private static INetworkNode getNodeFromBlockEntity(@Nullable BlockEntity blockEntity) {
        if (blockEntity != null) {
            INetworkNodeProxy<?> proxy = blockEntity.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY).orElse(null);
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

    public static InteractionResult attemptModify(Level level, BlockPos pos, Player player, Runnable action) {
        return attempt(level, pos, player, action, Permission.MODIFY);
    }

    public static InteractionResult attempt(Level level, BlockPos pos, Player player, Runnable action, Permission... permissionsRequired) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        INetworkNode node = getNodeAtPosition(level, pos);
        if (node == null) {
            return InteractionResult.SUCCESS;
        }

        INetwork network = getNetworkFromNode(node);

        if (network != null) {
            for (Permission permission : permissionsRequired) {
                if (!network.getSecurityManager().hasPermission(permission, player)) {
                    WorldUtils.sendNoPermissionMessage(player);

                    return InteractionResult.SUCCESS;
                }
            }
        }

        action.run();

        return InteractionResult.SUCCESS;
    }

    public static void extractBucketFromPlayerInventoryOrNetwork(Player player, INetwork network, Consumer<ItemStack> onBucketFound) {
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            ItemStack slot = player.getInventory().getItem(i);

            if (API.instance().getComparer().isEqualNoQuantity(StackUtils.EMPTY_BUCKET, slot)) {
                player.getInventory().removeItem(i, 1);

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
