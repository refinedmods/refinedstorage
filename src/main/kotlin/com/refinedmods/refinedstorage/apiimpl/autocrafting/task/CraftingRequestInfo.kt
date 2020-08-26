package com.refinedmods.refinedstorage.apiimpl.autocrafting.task

import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskReadException
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo
import com.refinedmods.refinedstorage.util.StackUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import reborncore.common.fluid.container.FluidInstance

class CraftingRequestInfo : ICraftingRequestInfo {
    override val item: ItemStack?
    override val fluid: FluidInstance?

    constructor(tag: CompoundTag) {
        if (!tag.getBoolean(NBT_FLUID)) {
            item = StackUtils.deserializeStackFromNbt(tag.getCompound(NBT_STACK))
            fluid = null
            if (item.isEmpty) {
                throw CraftingTaskReadException("Extractor stack is empty")
            }
        } else {
            fluid = FluidInstance()
            fluid.read(tag.getCompound(NBT_STACK))
            item = null
            if (fluid.isEmpty) {
                throw CraftingTaskReadException("Extractor fluid stack is empty")
            }
        }
    }

    constructor(item: ItemStack?) {
        this.item = item
        this.fluid = null
    }

    constructor(fluid: FluidInstance?) {
        this.fluid = fluid
        this.item = null
    }

    override fun writeToNbt(): CompoundTag {
        val tag = CompoundTag()
        tag.putBoolean(NBT_FLUID, fluid != null)
        if (fluid != null) {
            tag.put(NBT_STACK, fluid.write())
        } else {
            tag.put(NBT_STACK, StackUtils.serializeStackToNbt(item!!))
        }
        return tag
    }

    companion object {
        private const val NBT_FLUID = "Fluid"
        private const val NBT_STACK = "Stack"
    }
}