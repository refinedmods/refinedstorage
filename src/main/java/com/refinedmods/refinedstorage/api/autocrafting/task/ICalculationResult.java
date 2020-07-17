package com.refinedmods.refinedstorage.api.autocrafting.task;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;

import javax.annotation.Nullable;

/**
 * Returned from {@link ICraftingTask#calculate()} to indicate the result of the calculation.
 */
public interface ICalculationResult {
    /**
     * @return the type
     */
    CalculationResultType getType();

    /**
     * @return whether the calculation succeeded
     */
    boolean isOk();

    /**
     * If this result type is a {@link CalculationResultType#RECURSIVE}, the recursed pattern will be returned here.
     *
     * @return the recursed pattern, or null if this result is not {@link CalculationResultType#RECURSIVE}
     */
    @Nullable
    ICraftingPattern getRecursedPattern();
}
