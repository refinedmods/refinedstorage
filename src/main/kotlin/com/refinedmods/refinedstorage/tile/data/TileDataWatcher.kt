package com.refinedmods.refinedstorage.tile.data

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.network.tiledata.TileDataParameterMessage
import net.minecraft.entity.player.ServerPlayerEntity
import java.util.function.Consumer

class TileDataWatcher(player: ServerPlayerEntity, manager: TileDataManager?) {
    private var sentInitial = false
    private val player: ServerPlayerEntity
    private val manager: TileDataManager?
    private val cache: Array<Any?>
    fun getPlayer(): ServerPlayerEntity {
        return player
    }

    fun onClosed() {
        manager!!.removeWatcher(this)
    }

    fun detectAndSendChanges() {
        if (!sentInitial) {
            manager!!.parameters.forEach(Consumer { p: TileDataParameter<*, *>? -> sendParameter(true, p) })
            sentInitial = true
        } else {
            for (i in manager!!.watchedParameters.indices) {
                val parameter = manager.watchedParameters[i]
                val real = parameter!!.valueProducer.apply(manager.tile)
                val cached = cache[i]
                if (real != cached) {
                    cache[i] = real

                    // Avoid sending watched parameter twice (after initial packet)
                    if (cached != null) {
                        sendParameter(false, parameter)
                    }
                }
            }
        }
    }

    fun sendParameter(initial: Boolean, parameter: TileDataParameter<*, *>?) {
        RS.NETWORK_HANDLER.sendTo(player, TileDataParameterMessage(manager!!.tile, parameter, initial))
    }

    init {
        this.player = player
        this.manager = manager
        if (manager != null) {
            this.manager!!.addWatcher(this)
            cache = arrayOfNulls(manager.watchedParameters.size)
        }
    }
}