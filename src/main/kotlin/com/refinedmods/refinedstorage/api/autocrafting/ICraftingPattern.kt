package com.refinedmods.refinedstorage.api.autocrafting

import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskRegistry
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import reborncore.common.fluid.container.FluidInstance


/**
 * Represents a crafting pattern.
 */
interface ICraftingPattern {
    /**
     * @return the [ICraftingPatternContainer] where the pattern is in
     */
    fun getContainer(): ICraftingPatternContainer?

    /**
     * @return the crafting pattern stack
     */
    fun getStack(): ItemStack?

    /**
     * @return true if the crafting pattern is valid, false otherwise
     */
    fun isValid(): Boolean

    /**
     * @return an error message when this pattern is not valid, or null if there's no message
     */
    fun getErrorMessage(): Text?

    /**
     * @return true if the crafting pattern can be treated as a processing pattern, false otherwise
     */
    fun isProcessing(): Boolean

    /**
     * @return the inputs per slot
     */
    fun getInputs(): List<List<ItemStack>>

    /**
     * @return the outputs
     */
    fun getOutputs(): List<ItemStack>

    /**
     * @param took the items took per slot
     * @return the output based on the items took
     */
    fun getOutput(took: List<ItemStack>): ItemStack

    /**
     * @return the byproducts
     */
    fun getByproducts(): List<ItemStack>

    /**
     * @param took the items took per slot
     * @return the byproducts based on the items took
     */
    fun getByproducts(took: List<ItemStack>): List<ItemStack>

    /**
     * @return the fluid inputs per slot
     */
    fun getFluidInputs(): List<List<FluidInstance>>

    /**
     * @return the fluid outputs
     */
    fun getFluidOutputs(): List<FluidInstance>

    /**
     * @return the id of the factory that creates a crafting task for this pattern, as defined in the [ICraftingTaskRegistry]
     */
    fun getCraftingTaskFactoryId(): Identifier
}