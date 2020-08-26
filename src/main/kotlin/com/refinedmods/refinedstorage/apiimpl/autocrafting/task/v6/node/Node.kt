package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.node

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.SerializationUtil
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import reborncore.common.fluid.container.FluidInstance

abstract class Node {
    val isRoot: Boolean
    val pattern: ICraftingPattern
    var quantity = 0
        protected set
    protected var totalQuantity = 0
    val requirements = NodeRequirements()

    constructor(pattern: ICraftingPattern, root: Boolean) {
        this.pattern = pattern
        isRoot = root
    }

    constructor(network: INetwork, tag: CompoundTag) {
        quantity = tag.getInt(NBT_QUANTITY)
        totalQuantity = tag.getInt(NBT_QUANTITY_TOTAL)
        pattern = SerializationUtil.readPatternFromNbt(tag.getCompound(NBT_PATTERN), network.world)
        isRoot = tag.getBoolean(NBT_ROOT)
        requirements.readFromNbt(tag)
    }

    abstract fun update(network: INetwork, ticks: Int, nodes: NodeList, internalStorage: IStorageDisk<ItemStack>, internalFluidStorage: IStorageDisk<FluidInstance>, listener: NodeListener)
    open fun onCalculationFinished() {
        totalQuantity = quantity
    }

    fun addQuantity(quantity: Int) {
        this.quantity += quantity
    }

    protected operator fun next() {
        quantity--
    }

    open fun writeToNbt(): CompoundTag {
        var tag = CompoundTag()
        tag.putInt(NBT_QUANTITY, quantity)
        tag.putInt(NBT_QUANTITY_TOTAL, totalQuantity)
        tag.putBoolean(NBT_IS_PROCESSING, this is ProcessingNode)
        tag.putBoolean(NBT_ROOT, isRoot)
        tag.put(NBT_PATTERN, SerializationUtil.writePatternToNbt(pattern))
        tag = requirements.writeToNbt(tag)
        return tag
    }

    companion object {
        private const val NBT_PATTERN = "Pattern"
        private const val NBT_ROOT = "Root"
        private const val NBT_IS_PROCESSING = "IsProcessing"
        private const val NBT_QUANTITY = "Quantity"
        private const val NBT_QUANTITY_TOTAL = "TotalQuantity"
        @Throws(CraftingTaskReadException::class)
        fun fromNbt(network: INetwork, tag: CompoundTag): Node {
            return if (tag.getBoolean(NBT_IS_PROCESSING)) ProcessingNode(network, tag) else CraftingNode(network, tag)
        }
    }
}