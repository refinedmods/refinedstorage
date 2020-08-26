package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.calculator

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask

class CalculationResult : ICalculationResult {
    private val type: CalculationResultType
    private val recursedPattern: ICraftingPattern?
    private val previewElements: List<ICraftingPreviewElement<*>>
    private val craftingTask: ICraftingTask?

    constructor(type: CalculationResultType) {
        this.type = type
        recursedPattern = null
        previewElements = emptyList()
        craftingTask = null
    }

    constructor(type: CalculationResultType, recursedPattern: ICraftingPattern?) {
        this.type = type
        this.recursedPattern = recursedPattern
        previewElements = emptyList()
        craftingTask = null
    }

    constructor(type: CalculationResultType, previewElements: List<ICraftingPreviewElement<*>>, craftingTask: ICraftingTask) {
        this.type = type
        recursedPattern = null
        this.previewElements = previewElements
        this.craftingTask = craftingTask
    }

    override fun getType(): CalculationResultType {
        return type
    }

    override fun getPreviewElements(): List<ICraftingPreviewElement<*>> {
        return previewElements
    }

    override fun getTask(): ICraftingTask? = craftingTask
    override fun isOk(): Boolean = type === CalculationResultType.OK

    override fun getRecursedPattern(): ICraftingPattern? {
        return recursedPattern
    }
}