package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.CraftingTaskErrorType;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTaskError;

import javax.annotation.Nullable;

public class CraftingTaskError implements ICraftingTaskError {
    private CraftingTaskErrorType type;
    private ICraftingPattern recursedPattern;

    public CraftingTaskError(CraftingTaskErrorType type) {
        this.type = type;
    }

    public CraftingTaskError(CraftingTaskErrorType type, ICraftingPattern recursedPattern) {
        this.type = type;
        this.recursedPattern = recursedPattern;
    }

    @Override
    public CraftingTaskErrorType getType() {
        return type;
    }

    @Override
    @Nullable
    public ICraftingPattern getRecursedPattern() {
        return recursedPattern;
    }
}
