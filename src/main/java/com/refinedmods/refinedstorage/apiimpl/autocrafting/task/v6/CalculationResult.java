package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.CalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICalculationResult;

import javax.annotation.Nullable;

public class CalculationResult implements ICalculationResult {
    private final CalculationResultType type;
    private final ICraftingPattern recursedPattern;

    public CalculationResult(CalculationResultType type) {
        this.type = type;
        this.recursedPattern = null;
    }

    public CalculationResult(CalculationResultType type, ICraftingPattern recursedPattern) {
        this.type = type;
        this.recursedPattern = recursedPattern;
    }

    @Override
    public CalculationResultType getType() {
        return type;
    }

    @Override
    public boolean isOk() {
        return type == CalculationResultType.OK;
    }

    @Override
    @Nullable
    public ICraftingPattern getRecursedPattern() {
        return recursedPattern;
    }
}
