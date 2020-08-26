package com.refinedmods.refinedstorage.api.autocrafting.task

import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import reborncore.common.fluid.container.FluidInstance

/**
 * Contains information about a crafting request.
 */
interface ICraftingRequestInfo {
    /**
     * @return the item requested, or null if no item was requested
     */
    val item: ItemStack?

    /**
     * @return the fluid requested, or null if no fluid was requested
     */
    val fluid: FluidInstance?

    /**
     * @return the written tag
     */
    fun writeToNbt(): CompoundTag
}