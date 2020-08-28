package com.refinedmods.refinedstorage.util

import com.refinedmods.refinedstorage.RSComponents
import com.refinedmods.refinedstorage.api.component.INetworkNodeProxyComponent
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import dev.onyxstudios.cca.api.v3.block.BlockComponents
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import org.jetbrains.annotations.Nullable


object NetworkUtils {

    @Deprecated("fabric names", replaceWith = ReplaceWith("getNodeFromBlockEntity(tile)"))
    fun getNodeFromTile(tile: BlockEntity?): INetworkNode? = getNodeFromBlockEntity(tile)
    fun getNodeFromBlockEntity(blockEntity: BlockEntity?): INetworkNode? {
        blockEntity?:return null
        val proxy: INetworkNodeProxyComponent =
            BlockComponents.get(RSComponents.NETWORK_NODE_PROXY, blockEntity)?:return null
        return null
        //TODO
        //return proxy.node
    }

    @Nullable
    @Deprecated(replaceWith = ReplaceWith("node?.network"), message = "kotlin goes brr")
    fun getNetworkFromNode(@Nullable node: INetworkNode?): INetwork? {
        return node?.network
    }

    fun attemptModify(
        world: World,
        pos: BlockPos,
        facing: Direction,
        player: PlayerEntity,
        action: Runnable
    ): ActionResult? {
        return attempt(world, pos, facing, player, action, Permission.MODIFY)
    }

    fun attempt(
        world: World,
        pos: BlockPos,
        facing: Direction,
        player: PlayerEntity,
        action: Runnable,
        vararg permissionsRequired: Permission
    ): ActionResult {
        if (world.isClient) {
            return ActionResult.SUCCESS
        }
        val network: INetwork? = getNetworkFromNode(getNodeFromBlockEntity(world.getBlockEntity(pos)))
        if (network != null) {
            for (permission in permissionsRequired) {
                /*
                TODO security manager
                if (!network.getSecurityManager().hasPermission(permission, player)) {
                    WorldUtils.sendNoPermissionMessage(player)
                    return ActionResultType.SUCCESS
                }
                 */
            }
        }
        action.run()
        return ActionResult.SUCCESS
    }

    fun extractBucketFromPlayerInventoryOrNetwork(
        player: PlayerEntity,
        network: INetwork,
        onBucketFound: (ItemStack?) -> Unit
    ) {
        for (i in 0 until player.inventory.size()) {
            val slot: ItemStack = player.inventory.getStack(i)
            if (instance().comparer.isEqualNoQuantity(StackUtils.EMPTY_BUCKET, slot)) {
                player.inventory.removeStack(i, 1)
                onBucketFound(StackUtils.EMPTY_BUCKET.copy())
                return
            }
        }
        val fromNetwork = network.extractItem(StackUtils.EMPTY_BUCKET, 1, Action.PERFORM)
        if (!fromNetwork.isEmpty) {
            onBucketFound(fromNetwork)
        }
    }
}