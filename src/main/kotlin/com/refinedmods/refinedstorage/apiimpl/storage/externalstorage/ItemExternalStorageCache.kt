package com.refinedmods.refinedstorage.apiimpl.storage.externalstorage

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.apiimpl.API
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerimport
import java.util.*

class ItemExternalStorageCache {
    private var cache: MutableList<ItemStack>? = null
    fun update(network: INetwork?, @Nullable handler: IItemHandler?) {
        if (handler == null) {
            return
        }
        if (cache == null) {
            cache = ArrayList<ItemStack>()
            for (i in 0 until handler.getSlots()) {
                cache.add(handler.getStackInSlot(i).copy())
            }
            return
        }
        for (i in 0 until handler.getSlots()) {
            val actual: ItemStack = handler.getStackInSlot(i)
            if (i >= cache!!.size) { // ENLARGED
                if (!actual.isEmpty()) {
                    network.itemStorageCache.add(actual, actual.getCount(), false, true)
                    cache!!.add(actual.copy())
                }
                continue
            }
            val cached: ItemStack = cache!![i]
            if (!cached.isEmpty() && actual.isEmpty()) { // REMOVED
                network.itemStorageCache.remove(cached, cached.getCount(), true)
                cache!![i] = ItemStack.EMPTY
            } else if (cached.isEmpty() && !actual.isEmpty()) { // ADDED
                network.itemStorageCache.add(actual, actual.getCount(), false, true)
                cache!![i] = actual.copy()
            } else if (!API.instance().getComparer().isEqualNoQuantity(cached, actual)) { // CHANGED
                network.itemStorageCache.remove(cached, cached.getCount(), true)
                network.itemStorageCache.add(actual, actual.getCount(), false, true)
                cache!![i] = actual.copy()
            } else if (cached.getCount() != actual.getCount()) { // COUNT_CHANGED
                val delta: Int = actual.getCount() - cached.getCount()
                if (delta > 0) {
                    network.itemStorageCache.add(actual, delta, false, true)
                    cached.grow(delta)
                } else {
                    network.itemStorageCache.remove(actual, Math.abs(delta), true)
                    cached.shrink(Math.abs(delta))
                }
            }
        }
        if (cache!!.size > handler.getSlots()) { // SHRUNK
            for (i in cache!!.size - 1 downTo handler.getSlots()) { // Reverse order for the remove call.
                val cached: ItemStack = cache!![i]
                if (!cached.isEmpty()) {
                    network.itemStorageCache.remove(cached, cached.getCount(), true)
                }
                cache!!.removeAt(i)
            }
        }
        network.itemStorageCache.flush()
    }
}