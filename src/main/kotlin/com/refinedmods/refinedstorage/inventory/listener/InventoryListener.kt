package com.refinedmods.refinedstorage.inventory.listener

interface InventoryListener<T> {
    fun onChanged(handler: T, slot: Int, reading: Boolean)
}