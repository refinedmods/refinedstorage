package com.refinedmods.refinedstorage.apiimpl.storage.cache.listener

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener
import net.minecraft.entity.player.ServerPlayerEntityimport
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class ItemGridStorageCacheListener(player: ServerPlayerEntity, network: INetwork) : IStorageCacheListener<ItemStack?> {
    private val player: ServerPlayerEntity
    private val network: INetwork
    override fun onAttached() {
        RS.NETWORK_HANDLER.sendTo(player, GridItemUpdateMessage(network, network.securityManager.hasPermission(Permission.AUTOCRAFTING, player)))
    }

    override fun onInvalidated() {
        // NO OP
    }

    override fun onChanged(delta: StackListResult<ItemStack>?) {
        val deltas: MutableList<StackListResult<ItemStack>?> = ArrayList<StackListResult<ItemStack>?>()
        deltas.add(delta)
        onChangedBulk(deltas)
    }

    override fun onChangedBulk(deltas: List<StackListResult<ItemStack>?>?) {
        RS.NETWORK_HANDLER.sendTo(player, GridItemDeltaMessage(network, deltas))
    }

    init {
        this.player = player
        this.network = network
    }
}