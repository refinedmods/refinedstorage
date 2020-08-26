package com.refinedmods.refinedstorage.tile.data

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.network.tiledata.TileDataParameterUpdateMessage
import net.minecraft.block.entity.BlockEntity
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

class TileDataManager(
        val tile: BlockEntity
) {
    private val parameters: MutableList<TileDataParameter<*, *>> = ArrayList()
    private val watchedParameters: MutableList<TileDataParameter<*, *>> = ArrayList()
    private val watchers: MutableList<TileDataWatcher> = CopyOnWriteArrayList()

    fun addParameter(parameter: TileDataParameter<*, *>) {
        parameters.add(parameter)
    }

    fun getParameters(): List<TileDataParameter<*, *>> {
        return parameters
    }

    fun addWatchedParameter(parameter: TileDataParameter<*, *>) {
        addParameter(parameter)
        watchedParameters.add(parameter)
    }

    fun getWatchedParameters(): List<TileDataParameter<*, *>> {
        return watchedParameters
    }

    fun addWatcher(listener: TileDataWatcher) {
        watchers.add(listener)
    }

    fun removeWatcher(listener: TileDataWatcher?) {
        watchers.remove(listener)
    }

    fun sendParameterToWatchers(parameter: TileDataParameter<*, *>?) {
        watchers.forEach(Consumer { l: TileDataWatcher -> l.sendParameter(false, parameter) })
    }

    companion object {
        private var LAST_ID = 0
        private val REGISTRY: MutableMap<Int, TileDataParameter<*, *>> = HashMap()
        fun registerParameter(parameter: TileDataParameter<*, *>) {
            parameter.id = LAST_ID
            REGISTRY[LAST_ID++] = parameter
        }

        fun getParameter(id: Int): TileDataParameter<*, *>? {
            return REGISTRY[id]
        }

        fun setParameter(parameter: TileDataParameter<*, *>?, value: Any?) {
            RS.NETWORK_HANDLER.sendToServer(TileDataParameterUpdateMessage(parameter, value))
        }
    }

}