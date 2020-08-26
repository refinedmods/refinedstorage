package com.refinedmods.refinedstorage.util

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.function.Consumer

object NetworkUtils {

    fun getNodeFromTile(tile: BlockEntity?): INetworkNode? {
        if (tile != null) {
            // TODO Replace capability
//            val proxy: INetworkNodeProxy<*> = tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY).orElse(null)
//            if (proxy != null) {
//                return proxy.node
//            }
        }
        return null
    }

    fun getNetworkFromNode(node: INetworkNode?): INetwork? {
        return node?.network
    }

    fun attemptModify(world: World, pos: BlockPos?, facing: Direction?, player: PlayerEntity, action: Runnable): ActionResult {
        return attempt(world, pos, facing, player, action, Permission.MODIFY)
    }

    fun attempt(world: World, pos: BlockPos?, facing: Direction?, player: PlayerEntity, action: Runnable, vararg permissionsRequired: Permission?): ActionResult {
        if (world.isClient) {
            return ActionResult.SUCCESS
        }
        val network = getNetworkFromNode(getNodeFromTile(world.getBlockEntity(pos)))
        if (network != null) {
            for (permission in permissionsRequired) {
                if (!network.securityManager!!.hasPermission(permission, player)) {
                    WorldUtils.sendNoPermissionMessage(player)
                    return ActionResult.SUCCESS
                }
            }
        }
        action.run()
        return ActionResult.SUCCESS
    }

    fun extractBucketFromPlayerInventoryOrNetwork(player: PlayerEntity, network: INetwork, onBucketFound: Consumer<ItemStack?>) {
        for (i in 0 until player.inventory.size()) {
            val slot: ItemStack = player.inventory.getStack(i)
            if (instance().getComparer()!!.isEqualNoQuantity(StackUtils.EMPTY_BUCKET, slot)) {
                player.inventory.removeOne(slot)
                onBucketFound.accept(StackUtils.EMPTY_BUCKET.copy())
                return
            }
        }
        val fromNetwork = network.extractItem(StackUtils.EMPTY_BUCKET, 1, Action.PERFORM)
        if (!fromNetwork!!.isEmpty) {
            onBucketFound.accept(fromNetwork)
        }
    }
}