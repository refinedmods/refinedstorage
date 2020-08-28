package com.refinedmods.refinedstorage.api.autocrafting.task

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement

/**
 * The result of the crafting calculation.
 */
interface ICalculationResult {
    /**
     * @return the type
     */
    fun getType(): CalculationResultType?

    /**
     * @return get a list of [ICraftingPreviewElement]s
     */
    fun getPreviewElements(): List<ICraftingPreviewElement<*>>

    /**
     * @return the task if the calculation [.isOk], otherwise null
     */
    fun getTask(): ICraftingTask?

    /**
     * @return whether the calculation succeeded
     */
    fun isOk(): Boolean

    /**
     * If this result type is a [CalculationResultType.RECURSIVE], the recursed pattern will be returned here.
     *
     * @return the recursed pattern, or null if this result is not [CalculationResultType.RECURSIVE]
     */
    fun getRecursedPattern(): ICraftingPattern?
}