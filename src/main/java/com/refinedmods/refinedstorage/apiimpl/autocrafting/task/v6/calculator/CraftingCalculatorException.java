package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.calculator;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;

import javax.annotation.Nullable;

public class CraftingCalculatorException extends Exception {
    private final CalculationResultType type;
    @Nullable
    private final transient ICraftingPattern recursedPattern;

    public CraftingCalculatorException(CalculationResultType type) {
        this.type = type;
        this.recursedPattern = null;
    }

    public CraftingCalculatorException(CalculationResultType type, @Nullable ICraftingPattern recursedPattern) {
        this.type = type;
        this.recursedPattern = recursedPattern;
    }

    public CalculationResultType getType() {
        return type;
    }

    @Nullable
    public ICraftingPattern getRecursedPattern() {
        return recursedPattern;
    }
}
