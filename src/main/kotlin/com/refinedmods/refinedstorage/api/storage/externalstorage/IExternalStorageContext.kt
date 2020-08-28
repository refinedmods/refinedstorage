package com.refinedmods.refinedstorage.api.storage.externalstorage

import com.refinedmods.refinedstorage.api.storage.AccessType
import net.minecraft.item.ItemStack
import reborncore.common.fluid.container.FluidInstance


/**
 * Provides information about an external storage.
 */
interface IExternalStorageContext {
    /**
     * @return the priority of the external storage
     */
    fun getPriority(): Int

    /**
     * @return the access type of the external storage
     */
    fun getAccessType(): AccessType?

    /**
     * @param stack the stack to test
     * @return true if the external storage accepts the item, false otherwise
     */
    fun acceptsItem(stack: ItemStack?): Boolean

    /**
     * @param stack the stack to test
     * @return true if the external storage accepts the fluid, false otherwise
     */
    fun acceptsFluid(stack: FluidInstance?): Boolean
}