package com.refinedmods.refinedstorage.tile.data

interface TileDataParameterClientListener<T> {
    fun onChanged(initial: Boolean, value: T)
}