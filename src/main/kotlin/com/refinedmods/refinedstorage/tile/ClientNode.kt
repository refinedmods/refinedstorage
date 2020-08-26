package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import net.minecraft.item.ItemStack

class ClientNode(val stack: ItemStack, var amount: Int, val energyUsage: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is ClientNode) {
            false
        } else energyUsage == other.energyUsage && instance().getComparer()!!.isEqual(stack, other.stack)
    }

    override fun hashCode(): Int {
        var result = stack.hashCode()
        result = 31 * result + energyUsage
        return result
    }
}