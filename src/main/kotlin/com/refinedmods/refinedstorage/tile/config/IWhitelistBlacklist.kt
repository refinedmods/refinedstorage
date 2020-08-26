package com.refinedmods.refinedstorage.tile.config

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.item.ItemStack
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.tileentity.BlockEntity
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.items.IItemHandler
import java.util.function.BiConsumer
import java.util.function.Function

interface IWhitelistBlacklist {
    var whitelistBlacklistMode: Int

    companion object {
        fun <T> createParameter(): TileDataParameter<Int, T> where T : BlockEntity?, T : INetworkNodeProxy<*>? {
            return TileDataParameter<Int, T>(DataSerializers.VARINT, 0, Function { t: T -> (t!!.node as IWhitelistBlacklist).whitelistBlacklistMode }, BiConsumer { t: T, v: Int ->
                if (v == WHITELIST || v == BLACKLIST) {
                    (t!!.node as IWhitelistBlacklist).whitelistBlacklistMode = v
                }
            })
        }

        fun acceptsItem(filters: IItemHandler, mode: Int, compare: Int, stack: ItemStack?): Boolean {
            if (mode == WHITELIST) {
                for (i in 0 until filters.getSlots()) {
                    val slot: ItemStack = filters.getStackInSlot(i)
                    if (instance().getComparer()!!.isEqual(slot, stack!!, compare)) {
                        return true
                    }
                }
                return false
            } else if (mode == BLACKLIST) {
                for (i in 0 until filters.getSlots()) {
                    val slot: ItemStack = filters.getStackInSlot(i)
                    if (instance().getComparer()!!.isEqual(slot, stack!!, compare)) {
                        return false
                    }
                }
                return true
            }
            return false
        }

        fun acceptsFluid(filters: FluidInventory, mode: Int, compare: Int, stack: FluidInstance?): Boolean {
            if (mode == WHITELIST) {
                for (i in 0 until filters.getSlots()) {
                    val slot: FluidInstance? = filters.getFluid(i)
                    if (!slot.isEmpty() && instance().getComparer().isEqual(slot, stack, compare)) {
                        return true
                    }
                }
                return false
            } else if (mode == BLACKLIST) {
                for (i in 0 until filters.getSlots()) {
                    val slot: FluidInstance? = filters.getFluid(i)
                    if (!slot.isEmpty() && instance().getComparer().isEqual(slot, stack, compare)) {
                        return false
                    }
                }
                return true
            }
            return false
        }

        const val WHITELIST = 0
        const val BLACKLIST = 1
    }
}