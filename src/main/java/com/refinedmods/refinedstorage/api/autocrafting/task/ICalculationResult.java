package com.refinedmods.refinedstorage.api.autocrafting.task;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The result of the crafting calculation.
 */
public interface ICalculationResult {
    /**
     * @return the type
     */
    CalculationResultType getType();

    /**
     * @return get a list of {@link ICraftingPreviewElement}s
     */
    List<ICraftingPreviewElement<?>> getPreviewElements();

    /**
     * @return the task if the calculation {@link #isOk()}, otherwise null
     */
    @Nullable
    ICraftingTask getTask();

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
