package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.CraftingTaskCalculationResultType;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTaskCalculationResult;

import javax.annotation.Nullable;

public class CraftingTaskCalculationResult implements ICraftingTaskCalculationResult {
    private final CraftingTaskCalculationResultType type;
    private final ICraftingPattern recursedPattern;

    public CraftingTaskCalculationResult(CraftingTaskCalculationResultType type) {
        this.type = type;
        this.recursedPattern = null;
    }

    public CraftingTaskCalculationResult(CraftingTaskCalculationResultType type, ICraftingPattern recursedPattern) {
        this.type = type;
        this.recursedPattern = recursedPattern;
    }

    @Override
    public CraftingTaskCalculationResultType getType() {
        return type;
    }

    @Override
    public boolean isOk() {
        return type == CraftingTaskCalculationResultType.OK;
    }

    @Override
    @Nullable
    public ICraftingPattern getRecursedPattern() {
        return recursedPattern;
    }
}
