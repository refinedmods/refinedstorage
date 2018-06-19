package com.raoulvdberge.refinedstorage.api.autocrafting.task;

/**
 * The error type.
 */
public enum CraftingTaskErrorType {
    /**
     * When the crafting task would cause too much server strain or is too complex.
     */
    TOO_COMPLEX,
    /**
     * When one of the used patterns during the calculation reuses itself again and would cause an infinite loop.
     */
    RECURSIVE
}
