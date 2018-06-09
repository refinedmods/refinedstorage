package com.raoulvdberge.refinedstorage.api.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;

import javax.annotation.Nullable;

/**
 * Returned from {@link ICraftingTask#calculate()} when an error occurs during the calculation.
 */
public interface ICraftingTaskError {
    /**
     * @return the type
     */
    CraftingTaskErrorType getType();

    /**
     * If this error type is a {@link CraftingTaskErrorType#RECURSIVE}, the recursed pattern will be returned here.
     *
     * @return the recursed pattern, or null if this error is not {@link CraftingTaskErrorType#RECURSIVE}
     */
    @Nullable
    ICraftingPattern getRecursedPattern();
}
