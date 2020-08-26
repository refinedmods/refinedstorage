package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.calculator

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType

class CraftingCalculatorException : Exception {
    val type: CalculationResultType
    val recursedPattern: ICraftingPattern?

    constructor(type: CalculationResultType) {
        this.type = type
        recursedPattern = null
    }

    constructor(type: CalculationResultType, recursedPattern: ICraftingPattern?) {
        this.type = type
        this.recursedPattern = recursedPattern
    }
}