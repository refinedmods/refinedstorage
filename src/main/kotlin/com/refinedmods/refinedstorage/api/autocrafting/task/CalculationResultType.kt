package com.refinedmods.refinedstorage.api.autocrafting.task



/**
 * The result type.
 */
enum class CalculationResultType {
    /**
     * No problems.
     */
    OK,

    /**
     * Some requirements are missing.
     */
    MISSING,

    /**
     * There is no pattern for the requested stack.
     */
    NO_PATTERN,

    /**
     * When the crafting task would cause too much server strain or is too complex.
     */
    TOO_COMPLEX,

    /**
     * When one of the used patterns during the calculation reuses itself again and would cause an infinite loop.
     */
    RECURSIVE
}