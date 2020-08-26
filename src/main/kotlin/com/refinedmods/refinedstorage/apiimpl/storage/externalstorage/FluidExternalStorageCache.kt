package com.refinedmods.refinedstorage.apiimpl.storage.externalstorage

import com.refinedmods.refinedstorage.api.network.INetwork
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fluids.capability.IFluidHandlerimport
import java.util.*

class FluidExternalStorageCache {
    private var cache: MutableList<FluidInstance>? = null
    fun update(network: INetwork?, @Nullable handler: IFluidHandler?) {
        if (handler == null) {
            return
        }
        if (cache == null) {
            cache = ArrayList<FluidInstance>()
            for (i in 0 until handler.getTanks()) {
                cache.add(handler.getFluidInTank(i))
            }
            return
        }
        for (i in 0 until handler.getTanks()) {
            val actual: FluidInstance = handler.getFluidInTank(i)
            if (i >= cache!!.size) { // ENLARGED
                if (!actual.isEmpty()) {
                    network.fluidStorageCache.add(actual, actual.getAmount(), false, true)
                    cache!!.add(actual.copy())
                }
                continue
            }
            val cached: FluidInstance = cache!![i]
            if (actual.isEmpty() && cached.isEmpty()) { // NONE
                continue
            }
            if (actual.isEmpty() && !cached.isEmpty()) { // REMOVED
                network.fluidStorageCache.remove(cached, cached.getAmount(), true)
                cache!![i] = FluidInstance.EMPTY
            } else if (!actual.isEmpty() && cached.isEmpty()) { // ADDED
                network.fluidStorageCache.add(actual, actual.getAmount(), false, true)
                cache!![i] = actual.copy()
            } else if (!API.instance().getComparer().isEqual(actual, cached, IComparer.COMPARE_NBT)) { // CHANGED
                network.fluidStorageCache.remove(cached, cached.getAmount(), true)
                network.fluidStorageCache.add(actual, actual.getAmount(), false, true)
                cache!![i] = actual.copy()
            } else if (actual.getAmount() > cached.getAmount()) { // COUNT_CHANGED
                network.fluidStorageCache.add(actual, actual.getAmount() - cached.getAmount(), false, true)
                cached.setAmount(actual.getAmount())
            } else if (actual.getAmount() < cached.getAmount()) { // COUNT_CHANGED
                network.fluidStorageCache.remove(actual, cached.getAmount() - actual.getAmount(), true)
                cached.setAmount(actual.getAmount())
            }
        }
        if (cache!!.size > handler.getTanks()) { // SHRUNK
            for (i in cache!!.size - 1 downTo handler.getTanks()) { // Reverse order for the remove call.
                val cached: FluidInstance = cache!![i]
                if (!cached.isEmpty()) {
                    network.fluidStorageCache.remove(cached, cached.getAmount(), true)
                }
                cache!!.removeAt(i)
            }
        }
        network.fluidStorageCache.flush()
    }
}