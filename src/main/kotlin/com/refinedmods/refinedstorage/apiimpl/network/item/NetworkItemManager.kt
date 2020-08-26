package com.refinedmods.refinedstorage.apiimpl.network.item

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.IWirelessTransmitter
import com.refinedmods.refinedstorage.api.network.item.INetworkItem
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager
import com.refinedmods.refinedstorage.api.network.item.INetworkItemProvider
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.text.TranslationTextComponent
import java.util.concurrent.ConcurrentHashMap


class NetworkItemManager(private val network: INetwork) : INetworkItemManager {
    private val items: MutableMap<PlayerEntity?, INetworkItem?> = ConcurrentHashMap()
    override fun open(player: PlayerEntity?, stack: ItemStack?, slotId: Int) {
        var inRange = false
        for (node in network.nodeGraph!!.all()!!) {
            if (node is IWirelessTransmitter &&
                    network.canRun() &&
                    node.isActive && (node as IWirelessTransmitter).getDimension() === player!!.entityWorld.func_234923_W_()) {
                val transmitter = node as IWirelessTransmitter
                val pos: Vector3d = player.getPositionVec()
                val distance = Math.sqrt(Math.pow(transmitter.origin!!.x - pos.getX(), 2.0) + Math.pow(transmitter.origin!!.y - pos.getY(), 2.0) + Math.pow(transmitter.origin!!.z - pos.getZ(), 2.0))
                if (distance < transmitter.range) {
                    inRange = true
                    break
                }
            }
        }
        if (!inRange) {
            player!!.sendMessage(TranslationTextComponent("misc.refinedstorage.network_item.out_of_range"), player.getUniqueID())
            return
        }
        val item = (stack!!.item as INetworkItemProvider).provide(this, player, stack, slotId)
        if (item!!.onOpen(network)) {
            items[player] = item
        }
    }

    override fun close(player: PlayerEntity?) {
        items.remove(player)
    }

    override fun getItem(player: PlayerEntity?): INetworkItem? {
        return items[player]
    }

    override fun drainEnergy(player: PlayerEntity?, energy: Int) {
        val item = getItem(player)
        item?.drainEnergy(energy)
    }
}